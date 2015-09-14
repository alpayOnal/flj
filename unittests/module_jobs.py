# -*- coding: utf8 -*-
import unittest
from basetest import BaseTest
from bson.objectid import ObjectId
import arrow
from app.modules.jobs import Jobs


class TestWidget(BaseTest, unittest.TestCase):
    """docstring for TestHistory"""

    JOBS = [{
        "date": arrow.utcnow().replace(hours=-10).naive,
        "title": "Receptionist - Fluent French Speaker",
        "description": "At KLMN, we are looking for experienced French speakers to work at one ...",
        "company": "1X Hotels",
        "salary": "Competitive + Benefits",
        "jobType": "Contract/Interim",
        "source": "flj.com",
        "location": {
          "country": "United Kingdom",
          "state": "",
          "city": "London",
          "region": "Westcost",
          "lat": 412,
          "long": 121
        },
        "stats": {
          "viewed": 10,
          "starred": 4
        }
    }, {
        "date": arrow.utcnow().replace(hours=+2).naive,
        "title": "Waitress Fluent German Speaker",
        "description": "Office & Accounts Administrator - Construction",
        "company": "Tenders",
        "salary": "£21,000 to £23,000 p.a. plus some fantastic benefit",
        "jobType": "part-time",
        "source": "dublinxyzjobs.com",
        "location": {
          "country": "Ireland",
          "state": "",
          "city": "Dublin",
          "region": "central",
          "lat": 212,
          "long": 321
        },
        "stats": {
          "viewed": 64,
          "starred": 14
        }
    }, {
        "date": arrow.utcnow().replace(hours=-8).naive,
        "title": "Payroll and Benefits Administrator",
        "description": "One of my top London based Media clients urgently seeks a talented Payroll and Benefits Administrator ",
        "company": "Harriet Rawlinson",
        "salary": "£10 - £15 p hour",
        "jobType": "PART TIME",
        "source": "flj.com",
        "location": {
          "country": "United Kingdom",
          "state": "",
          "city": "Manchester",
          "region": "North",
          "lat": 312,
          "long": 21
        },
        "stats": {
          "viewed": 82,
          "starred": 2
        }
    }, {
        "date":  arrow.utcnow().replace(hours=-3).naive,
        "title": "Trainee Personal Trainers",
        "description": "One of my top London based Media clients urgently seeks a talented Payroll and Benefits Administrator ",
        "company": "Blink",
        "salary": "Up to £37,000",
        "jobType": "permanent",
        "source": "flj.com",
        "location": {
          "country": "United Kingdom",
          "state": "",
          "city": "London",
          "region": "North",
          "lat": 421,
          "long": 121
        },
        "stats": {
          "viewed": 53,
          "starred": 7
        }
    }
    ]

    @classmethod
    def setUpClass(cls):
        cls.jobsModule = Jobs(cls.config)

    def test_00_insert(self):
        for job in self.JOBS:
            job = self.jobsModule.insert(job)
            self.assertIsInstance(job['id'], ObjectId)

    def test_01_getWithoutFilter(self):
        jobsFound = self.jobsModule.get(filtering={})
        self.assertEqual(jobsFound.count(), len(self.JOBS))

    def test_02_getOne(self):
        job = self.JOBS[0]
        jobFound = self.jobsModule.getOne(job['id'])
        self.assertEqual(job['id'], jobFound['id'])

    def test_03_increaseViewed(self):
        job = self.JOBS[1]
        jobId = job['id']
        self.jobsModule.increaseView(jobId)
        jobFound = self.jobsModule.getOne(jobId)

        self.assertEqual(
            job['stats']['viewed'] + 1, jobFound['stats']['viewed'])

    def test_03_increaseStarred(self):
        job = self.JOBS[1]
        jobId = job['id']
        self.jobsModule.increaseStar(jobId)
        jobFound = self.jobsModule.getOne(jobId)
        self.assertEqual(
            job['stats']['starred'] + 1, jobFound['stats']['starred'])

    def test_03_decreaseStarred(self):
        job = self.JOBS[0]
        jobId = job['id']
        self.jobsModule.decreaseStar(jobId)
        jobFound = self.jobsModule.getOne(jobId)
        self.assertEqual(
            job['stats']['starred'] - 1, jobFound['stats']['starred'])

    def test_0401_getByLocationFilter(self):
        def doFilter(job, count, countWithRegion):
            filtering = {
                "location": {
                    "country": job["location"]["country"].upper(),
                    "city": job["location"]["city"].lower(),
                }
            }

            jobsFound = self.jobsModule.get(filtering=filtering)
            self.assertEqual(count, jobsFound.count())

            filtering['location']['region'] = job["location"]["region"].upper()
            jobsFound = self.jobsModule.get(filtering=filtering)
            self.assertEqual(countWithRegion, jobsFound.count())

        doFilter(self.JOBS[3], 2, 1)  # 3 london jobs, 1 westcost jobs
        doFilter(self.JOBS[1], 1, 1)  # 1 dublin job, 1 central job

    def test_0402_getByKeywordFilter(self):
        def doFilter(keyword, count):
            filtering = {
                "title": keyword
            }
            jobsFound = self.jobsModule.get(filtering=filtering)
            self.assertEqual(count, jobsFound.count())
        doFilter("geRM", 1)  # 1 greman job
        doFilter("speak", 2)  # 3 jobs containg *speak* in title

    def test_0403_getByJobTypeFilter(self):
        def doFilter(keyword, count):
            filtering = {
                "jobType": keyword
            }
            jobsFound = self.jobsModule.get(filtering=filtering)
            self.assertEqual(count, jobsFound.count())
        doFilter("part", 2)  # 2 part time jobs
        doFilter("peRManent", 1)  # 1 permanent job

    def test_0403_getBySinceMaxFilter(self):
        jobsFound = list(self.jobsModule.get(filtering={}))
        sinceJob = jobsFound[1]
        maxJob = jobsFound[3]
        targetJob = jobsFound[2]

        filtering = {
            "sinceId": sinceJob["_id"],
            "maxId": maxJob["_id"]
        }

        jobsFound = list(self.jobsModule.get(filtering=filtering))
        self.assertEqual(jobsFound[0]["_id"], targetJob["_id"])

    def test_0405_getByCombinedFilter(self):
        def doFilter(job, keyword, jobType, count):
            filtering = {
                "location": {
                    "country": job["location"]["country"].upper(),
                    "city": job["location"]["city"].lower(),
                },
                "title": keyword,
                "description": keyword,
                "jobType": jobType
            }
            jobsFound = self.jobsModule.get(filtering=filtering)
            self.assertEqual(count, jobsFound.count())

        job = self.JOBS[1]
        doFilter(job, "speak", job['jobType'], 1)  # 1 speaker in Dublin
        doFilter(job, "speak", "naaa", 0)  # 0 speaker job with type naaa in Dublin

        job = self.JOBS[0]
        doFilter(job, "speak", job['jobType'], 1)  # 2 part time jobs
        doFilter(job, "one of my", "permanent", 1)  # 2 part time jobs

    def test_0406_getNewestJobBySource(self):
        job = self.JOBS[3]
        source = job["source"]
        jobFound = self.jobsModule.getNewestBySource(source)
        self.assertEqual(jobFound["id"], job["id"])

    def test_0407_getNewestJobBySource(self):
        job = self.JOBS[1]
        jobFound = self.jobsModule.getNewest(filtering={})
        self.assertEqual(jobFound["id"], job["id"])

    # @TODO: delete method test

    def test_0406_getByIdsFilter(self):
        ids = [self.JOBS[0]["id"], self.JOBS[1]["id"]]
        filtering = {
            "ids": ids
        }
        jobsFound = self.jobsModule.get(filtering=filtering)
        self.assertEqual(2, jobsFound.count())


unittest.main()
