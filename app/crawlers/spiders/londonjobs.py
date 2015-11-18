import scrapy
from scrapy import FormRequest, Request, Selector
from scrapy.http.cookies import CookieJar
import arrow
import dateparser
from ..items import JobItem
from modules.jobs import Jobs
from .. import config, CrawlerLogs


class Londonjobs(scrapy.Spider):
    name = "londonjobs"
    allowed_domains = ["londonjobs.co.uk"]
    start_urls = [
        "http://www.londonjobs.co.uk/cgi-bin/advancedsearch.cgi"
    ]
    orderedSearchUrl = "http://www.londonjobs.co.uk/cgi-bin/advsearch?reord=D&\
lr=&repeat_search=Y&agency_group_id=&vac_group_id=&agency_id=&\
vr=&agency_group_id=&agency_id=&vr=&engine=db"

    def __init__(self):
        super(Londonjobs, self).__init__()

        CrawlerLogs().insert(self.name, self.start_urls[0])

        jobs = Jobs(config)
        try:
            self.lastSavedJob = jobs.getNewestBySource(self.name)
            self.lastSavedJob["date"] = arrow.get(self.lastSavedJob["date"])
        except Exception, e:
            self.logger.warning("last saved job not found.")
            self.lastSavedJob = None

    def close_spider(self, spider):
        self.logger.info("{} new job entries are found.".format(
            self.newJobCounter))
        self.client.close()

    def parse(self, response):
        """
        make advanced search to go to job posts list and acquire cookies.
        lastly, it redirects to the ordered jobs list url.
        :param response:
        :return request:
        """
        CrawlerLogs().insert(self.name, "FormRequest: psform")
        yield FormRequest.from_response(
            response, formname='psform',
            formdata={
                'daysback': "1", 'location_within': "50"},
            callback=self.parseSearchList)

    @staticmethod
    def redirect(response, nextURL, callback):
        cookieJar = response.meta.setdefault('cookie_jar', CookieJar())
        cookieJar.extract_cookies(response, response.request)
        request = Request(
            nextURL,
            callback,
            meta={'dont_merge_cookies': True, 'cookie_jar': cookieJar})
        cookieJar.add_cookie_header(request)
        return request

    def isFirstSeen(self, date, link, title):
        """
        check whether the date is newer than the last saved job's date.

        :param date: arrow instance
        :param link: relative link of job post
        :param title: title of job post
        :return: if date is newer, returns True
        """
        if self.lastSavedJob:
            oldDate = self.lastSavedJob["date"]
        else:
            oldDate = arrow.utcnow().replace(years=-1)

        return oldDate < date

    def parseSearchList(self, response):
        yield Londonjobs.redirect(
            response, self.orderedSearchUrl, self.parseOrderedSearchList)

    def parseOrderedSearchList(self, response):
        # fetching only jobs segments whose class contains "lineage" because
        # these posts are being displayed in a standard format.
        self.logger.info("parsing ordered sort list")

        def callbackWrapper(date, link, title, salary, address, jobType):
            def callback(response):
                """
                this inline method acts as a proxy to pass date field to
                parseJobPage() because in job pages date is not
                presented properly.
                :param response:
                :return:
                """
                yield self.parseJobPage(
                    response, date, link, title, salary, address, jobType)
            return callback

        jobDetailXpath = '//*[@id="vacs"]/div[contains(@class, "semi") or contains(@class, "lineage")]'
        linkPath = ".//h3/a/@href"
        titlePath = ".//h3/a/text()"
        salaryPath = './/dd[1]/text()'
        addressPath = './/dd[2]/text()'
        jobTypePath = './/dd[3]/text()'
        datePath = ".//dd[4]/text()"
        jobSegments = response.xpath(jobDetailXpath)
        self.logger.info("{} job segment found.".format(len(jobSegments)))

        newJobCounter = 0
        for sel in jobSegments:
            self.crawler.stats.inc_value("scanned jobs")
            date = sel.xpath(datePath).extract()
            if not date:
                self.logger.error("cannot extract date {}".format(
                    sel.extract()))
                continue

            link = sel.xpath(linkPath).extract()[0]
            title = sel.xpath(titlePath).extract()[0]
            salary = sel.xpath(salaryPath).extract()[0]
            address = sel.xpath(addressPath).extract()[0]
            jobType = sel.xpath(jobTypePath).extract()[0]
            date = arrow.get(
                dateparser.parse(date[0]), "Europe/London").to("UTC")

            if not self.isFirstSeen(date, link, title):
                """
                since crawled jobs are sorted by date from newer to older,
                if date is not first seen, then it must be already saved and,
                that is, we have completed with new job posts.
                """
                self.logger.info(
                    "reached the last saved job's date. {} {} {} {}".format(
                        date, title,
                        self.lastSavedJob["date"],
                        self.lastSavedJob["title"]
                    ))
                CrawlerLogs.insert(
                    self.name, self.orderedSearchUrl,
                    len(jobSegments), newJobCounter)
                return

            self.crawler.stats.inc_value("new jobs")
            newJobCounter += 1
            yield Londonjobs.redirect(
                response, response.urljoin(link),
                callbackWrapper(date, link, title, salary, address, jobType))
        CrawlerLogs.insert(
            self.name, self.orderedSearchUrl, len(jobSegments), newJobCounter)

    def parseJobPage(
            self, response, date, link, title, salary, address, jobType):

        try:
            company = response.xpath(
                '//*[@id="vacPlacedBy"]/p[2]/a[2]/text()').extract()[0]
        except:
            try:
                company = response.xpath(
                    '//*[@id="candidateOptions"]/div[2]/div/address//text()'
                ).extract()[0]
            except:
                self.logger.exception(
                    "cannot extract company name from {}".format(link))
                return

        try:
            description = "\n".join(
                [i.strip() for i in response.xpath(
                    '//*[@id="vacancyDetails"]/p//text()').extract()])
            if not description:
                raise Exception("description not found")
        except Exception, e:
            description = "\n".join(
                [i.strip() for i in response.xpath(
                    '//*[@id="vacancyDetails"]//text()').extract()])

        job = JobItem()
        job["source"] = self.name
        job["date"] = date
        job["title"] = title
        job["description"] = description
        job["jobType"] = jobType
        job["salary"] = salary
        job["meta"] = {
            "url": link
        }
        job["location"] = {
            "city": "london",
            "country": "united kingdom",
            "lat": 51.507995,
            "long": -0.108747,
            "region": "",
            "state": "",
            "address": address
        }
        job["company"] = company
        return job
