# -*- coding: utf8 -*-
# from app.libraries import loggerFactory
# from app.libraries.mongodb import getDb
# from app.modules.errors import (NotFoundException,
#                                 WrongArgumentException)


# class NotificationManager(object):
#     """docstring for NotificationManager"""
#     def __init__(self, config):
#         super(NotificationManager, self).__init__()
#         self.config = config

#     def receiveJobs(self):
#         # indeksle
#         #
#         pass

#     def sendNotification(self, account):
#         pass

from collections import defaultdict


class NotificationManager(object):
    """
    This class is responsible for the works relevant to the alarm
    notifications and can be regarded as the entry point of notification layer;
    that is, other modules will interact with only this class.
    """
    def __init__(self):
        super(NotificationManager, self).__init__()
        self.matcher = Matcher()
        # self.ntfNuilder = NotificationBuilder()
        # self.accountCollection = Accounts()

    def setJobs(self, jobsIterator):
        self.matcher.setJobs(jobsIterator)
        return self

    def start(self):
        accounts = [{
            "id": 1,
            "alarms": [
                {
                    "keywords": ["armut"]
                },
                {
                    "keywords": ["elma"]
                }
            ]
        }, {
            "id": 2,
            "alarms": [
                {
                    "keywords": ["kav"]
                },
                {
                    "keywords": ["elma"]
                }
            ]
        }]
        for account in accounts:
            try:
                matches = self.matcher.match(account["alarms"])
                notification = Notification(matches)
                print "account: ", account["id"]
                print notification.getTitle()
                print notification
                # publisher.publish(notification)

                # ntfStatus = NotificationStatus(
                #     update_time=arrow.utcnow(),
                #     latestJobId=matches.getLatestId())
                # self.accountCollection.saveNotifiactionStatus(ntfStatus)
            except Exception, e:
                print e.message
                emsg = "notification builder error. accountId <{}>".format
                # logger.exception(emsg(account["id"]))


class Notification(object):
    """docstring for NotificationBuilder"""
    TITLE = "{count} new jobs matching with your alarms"
    DETAIL_ITEM = "{position}) {title}"
    ITEM_DELIMITER = "\n"
    MAX_ITEM = 2

    def __init__(self, matches):
        super(Notification, self).__init__()
        self.matches = matches

    def getTitle(self):
        return self.TITLE.format(count=self.matches.count())

    def getDetail(self):
        items = []
        for k, i in enumerate(self.matches.getJobs()):
            if k == self.MAX_ITEM:
                items.append("...")
                break
            items.append(self.DETAIL_ITEM.format(
                **{"position": k + 1, "title": i["title"]}))

        return self.ITEM_DELIMITER.join(items)

    def __str__(self):
        return self.getDetail()


class Matches(object):
    """Represents a job and alarm matching and generally used by
    the class Matcher"""
    def __init__(self):
        super(Matches, self).__init__()
        self.matches = {}

    def insert(self, job, alarm):
        matching = self.matches.get(job["id"], {"alarms": [], "job": None})
        matching["alarms"].append(alarm)
        matching["job"] = job
        self.matches[job["id"]] = matching
        return self

    def getJobs(self):
        return (i["job"] for i in self.matches.values())

    def count(self):
        return len(self.matches)


class Matcher(object):
    """This class matches given alarms with given jobs"""
    tokenSplitter = " "

    def __init__(self):
        super(Matcher, self).__init__()

    @classmethod
    def extractWords(cls, text):
        return text.lower().split(cls.tokenSplitter)

    def startIndexing(self):
        for position, job in enumerate(self.jobs):
            words = self.extractWords(job["title"])
            for word in words:
                wordIndex = self.jobsIndex[word]
                wordIndex[position] = True
                self.jobsIndex[word] = wordIndex

    def findMatchingJobs(self, alarm):
        haystack = self.jobsIndex.keys()
        found = []
        for keyword in alarm["keywords"]:
            for word in haystack:
                if keyword.lower() in word:
                    jobPositions = self.jobsIndex[word].keys()
                    found += jobPositions
        return list(set(found))

    def setJobs(self, jobsIterator):
        self.jobs = jobsIterator
        self.jobsIndex = defaultdict(dict)
        self.startIndexing()
        return self

    def match(self, alarms):
        matches = Matches()
        for alarm in alarms:
            matchingJobPositions = self.findMatchingJobs(alarm)
            for position in matchingJobPositions:
                matches.insert(self.jobs[position], alarm)
        return matches


if __name__ == '__main__':
    from pprint import pprint as pp
    cursor = [
        {
            "id": 1,
            "title": "elma armut patates"
        }, {
            "id": 2,
            "title": "patates domates patlican"
        }, {
            "id": 3,
            "title": "patlican karpuz kavun"
        }, {
            "id": 4,
            "title": "elma karpuz patates"
        }
    ]
    accounts = [{
        "id": 1,
        "alarms": [
            {
                "keywords": ["armut"]
            },
            {
                "keywords": ["elma"]
            }
        ]
    }]

    matches = NotificationManager().setJobs(cursor).start()
    # print matches.count()
    # for k in matches.getJobs():
        # print k
