# -*- coding: utf8 -*-
import unittest
from basetest import BaseTest
from bson.objectid import ObjectId
import arrow
from app.modules.matcher import Matcher, Matchings
from app.modules.notifications import NotificationManager, Notification


class TestNotifications(BaseTest, unittest.TestCase):
    """docstring for TestHistory"""

    JOBS = [{
        "id": ObjectId(),
        "date": arrow.utcnow().replace(hours=-10).naive,
        "title": "Software Engineer, Internal Products",
        "description": "Work and experiment with technologies of your choice from React, Node.js to Python, Go, Scala, etc.Ability to work anywhere in the technical stack, delivering quality code both on the frontend and backend",
        "location": {
          "country": "United Kingdom",
          "state": "",
          "city": "London",
          "region": "Westcost",
          "lat": 412,
          "long": 121
        }
    }, {
        "id": ObjectId(),
        "date": arrow.utcnow().replace(hours=+2).naive,
        "title": "Python Engineer",
        "description": "If you’re an ambitious Python Software Engineer and thrive on working on tough, real-world problems, you want to work here. We are building a data center management product (FUEL) which can become the standard choice of a simple but powerful OpenStack deployment tool. We want you to bring ideas of how it should look and implement it. With the tool presently used to deploy OpenStack at the moment, we have an ambitious roadmap which includes live upgrades, network devices management, SDN devices integration and many more.",
        "location": {
          "country": "Ireland",
          "state": "",
          "city": "Dublin",
          "region": "Westcost",
          "lat": 412,
          "long": 121
        }
    }, {
        "id": ObjectId(),
        "date": arrow.utcnow().replace(hours=-8).naive,
        "title": "Android Developer",
        "description": "The ability to read and understand PHP (the language of our API and web framework).",
        "location": {
          "country": "United Kingdom",
          "state": "",
          "city": "London",
          "region": "Westcost",
          "lat": 412,
          "long": 121
        }
    }, {
        "id": ObjectId(),
        "date": arrow.utcnow().replace(hours=-3).naive,
        "title": "We want to speak to PHP Developers!",
        "description": "MyBuilder is enjoying unprecedented growth and success. We need to grow our talented tech team so are hiring great PHP Developers at all levels of experience.",
        "location": {
          "country": "Ireland",
          "state": "Dublin",
          "city": "Dublin",
          "region": "Westcost",
          "lat": 412,
          "long": 121
        }
    }, {
        "id": ObjectId(),
        "date": arrow.utcnow().replace(hours=-3).naive,
        "title": "android php python",
        "description": "MyBuilder is enjoying unprecedented growth and success. We need to grow our talented tech team so are hiring great PHP Developers at all levels of experience.",
        "location": {
          "country": "Spain",
          "state": "",
          "city": "Barcelona",
          "region": "",
          "lat": 412,
          "long": 121
        }
    }
    ]

    ACCOUNTS = [{
        "id": 1,
        "gcmId": 15,
        "alarms": [
            {
                "id": "11",
                "keywords": [
                    "pHp",
                    "pYthon"
                ],
                "location": {
                    "city": "london",
                    "country": "united kingdom",
                    "state": ""
                }
            }
        ]
    }, {
        "id": 2,
        "gcmId": 25,
        "alarms": [
            {  # matches
                "matches": {"count": 2},
                "id": "21",
                "keywords": [
                    "php",
                    "python"
                ],
                "location": {
                    "city": "dublin",
                    "country": "Ireland",
                    "state": ""
                }
            }, {  # matches
                "matches": {"count": 1},
                "id": "22",
                "keywords": [
                    "pYTHon"
                ],
                "location": {
                    "city": "dublin",
                    "country": "IREland",
                    "state": "Dublin"
                }
            }, {  # matches
                "matches": {"count": 1},
                "id": "23",
                "keywords": [
                    "android"
                ],
                "location": {
                    "city": "london",
                    "country": "united kingdom",
                    "state": ""
                }
            }, {  # does not match
                "matches": {"count": 0},
                "id": "24",
                "keywords": [
                    "android"
                ],
                "location": {
                    "city": "dublin",
                    "country": "ireland",
                    "state": ""
                }
            }, {  # matches
                "matches": {"count": 1},
                "id": "25",
                "keywords": [
                    "PHP"
                ],
                "location": {
                    "city": "dublin",
                    "country": "ireland",
                    "state": ""
                }
            }, {  # does not matches
                "matches": {"count": 0},
                "id": "26",
                "keywords": [
                    "delphi"
                ],
                "location": {
                    "city": "dublin",
                    "country": "ireland",
                    "state": ""
                }
            }
        ]
    }, {
        "id": 3,
        "gcmId": 35,
        "alarms": [
            {
                "id": "31",
                "keywords": [
                    "pHp",
                    "pYthon"
                ],
                "location": {
                    "city": "berlin",
                    "country": "germany",
                    "state": ""
                }
            }
        ]
    }]

    @classmethod
    def setUpClass(cls):
        cls.matches = Matcher()

    def test_00_match(self):
        matcher = Matcher()
        account = self.ACCOUNTS[0]
        matchings = matcher.setJobs(self.JOBS).match(account["alarms"])
        self.assertEqual(matchings.count(), 2)
        self.assertIn(str(self.JOBS[0]["id"]), matchings.getJobIDs())
        self.assertIn(str(self.JOBS[2]["id"]), matchings.getJobIDs())

    def test_01_match(self):
        matcher = Matcher()
        matcher.setJobs(self.JOBS)
        account = self.ACCOUNTS[1]
        alarms = account["alarms"]
        for alarm in account["alarms"]:
            matchings = matcher.match([alarm])
            self.assertEqual(matchings.count(), alarm["matches"]["count"])

    def test_02_matching_alarm(self):
        matcher = Matcher()
        account = self.ACCOUNTS[1]
        matchings = matcher.setJobs(self.JOBS).match(account["alarms"])
        matchingAlarmCount = len(list(matchings.getAlarms()))
        self.assertEqual(matchingAlarmCount, 4)

    def test_02_no_matching(self):
        matcher = Matcher()
        account = self.ACCOUNTS[2]
        matchings = matcher.setJobs(self.JOBS).match(account["alarms"])
        self.assertEqual(matchings.count(), 0)

    def test_03_notification(self):
        matcher = Matcher()
        account = self.ACCOUNTS[0]
        matchings = matcher.setJobs(self.JOBS).match(account["alarms"])
        notification = Notification(matchings)

        self.assertIn("Android Developer", str(notification))
        self.assertIn("Software Engineer", str(notification))
        self.assertIn("alarmID:", repr(notification))
        self.assertIn("jobIDs:", repr(notification))

        nID = notification.genNotificationID()
        self.assertIn("jobIDs", nID)
        self.assertIn("createdAt", nID)
        self.assertIn("matchingCount", nID)

    # def test_01_getWithoutFilter(self):
    #     jobsFound = self.jobsModule.get(filtering={})
    #     self.assertEqual(jobsFound.count(), len(self.JOBS))

    # def test_02_getOne(self):
    #     job = self.JOBS[0]
    #     jobFound = self.jobsModule.getOne(job['id'])
    #     self.assertEqual(job['id'], jobFound['id'])

    # def test_03_increaseViewed(self):
    #     job = self.JOBS[1]
    #     jobId = job['id']
    #     self.jobsModule.increaseView(jobId)
    #     jobFound = self.jobsModule.getOne(jobId)

    #     self.assertEqual(
    #         job['stats']['viewed'] + 1, jobFound['stats']['viewed'])

    # def test_03_increaseStarred(self):
    #     job = self.JOBS[1]
    #     jobId = job['id']
    #     self.jobsModule.increaseStar(jobId)
    #     jobFound = self.jobsModule.getOne(jobId)
    #     self.assertEqual(
    #         job['stats']['starred'] + 1, jobFound['stats']['starred'])

    # def test_03_decreaseStarred(self):
    #     job = self.JOBS[0]
    #     jobId = job['id']
    #     self.jobsModule.decreaseStar(jobId)
    #     jobFound = self.jobsModule.getOne(jobId)
    #     self.assertEqual(
    #         job['stats']['starred'] - 1, jobFound['stats']['starred'])

    # def test_0401_getByLocationFilter(self):
    #     def doFilter(job, count, countWithRegion):
    #         filtering = {
    #             "location": {
    #                 "country": job["location"]["country"].upper(),
    #                 "city": job["location"]["city"].lower(),
    #             }
    #         }

    #         jobsFound = self.jobsModule.get(filtering=filtering)
    #         self.assertEqual(count, jobsFound.count())

    #         filtering['location']['region'] = job["location"]["region"].upper()
    #         jobsFound = self.jobsModule.get(filtering=filtering)
    #         self.assertEqual(countWithRegion, jobsFound.count())

    #     doFilter(self.JOBS[3], 2, 1)  # 3 london jobs, 1 westcost jobs
    #     doFilter(self.JOBS[1], 1, 1)  # 1 dublin job, 1 central job

    # def test_0402_getByKeywordFilter(self):
    #     def doFilter(keyword, count):
    #         filtering = {
    #             "title": keyword
    #         }
    #         jobsFound = self.jobsModule.get(filtering=filtering)
    #         self.assertEqual(count, jobsFound.count())
    #     doFilter("geRM", 1)  # 1 greman job
    #     doFilter("speak", 2)  # 3 jobs containg *speak* in title

    # def test_0403_getByJobTypeFilter(self):
    #     def doFilter(keyword, count):
    #         filtering = {
    #             "jobType": keyword
    #         }
    #         jobsFound = self.jobsModule.get(filtering=filtering)
    #         self.assertEqual(count, jobsFound.count())
    #     doFilter("part", 2)  # 2 part time jobs
    #     doFilter("peRManent", 1)  # 1 permanent job

    # def test_0403_getBySinceMaxFilter(self):
    #     jobsFound = list(self.jobsModule.get(filtering={}))
    #     sinceJob = jobsFound[1]
    #     maxJob = jobsFound[3]
    #     targetJob = jobsFound[2]

    #     filtering = {
    #         "sinceId": sinceJob["_id"],
    #         "maxId": maxJob["_id"]
    #     }

    #     jobsFound = list(self.jobsModule.get(filtering=filtering))
    #     self.assertEqual(jobsFound[0]["_id"], targetJob["_id"])

    # def test_0405_getByCombinedFilter(self):
    #     def doFilter(job, keyword, jobType, count):
    #         filtering = {
    #             "location": {
    #                 "country": job["location"]["country"].upper(),
    #                 "city": job["location"]["city"].lower(),
    #             },
    #             "title": keyword,
    #             "description": keyword,
    #             "jobType": jobType
    #         }
    #         jobsFound = self.jobsModule.get(filtering=filtering)
    #         self.assertEqual(count, jobsFound.count())

    #     job = self.JOBS[1]
    #     doFilter(job, "speak", job['jobType'], 1)  # 1 speaker in Dublin
    #     doFilter(job, "speak", "naaa", 0)  # 0 speaker job with type naaa in Dublin

    #     job = self.JOBS[0]
    #     doFilter(job, "speak", job['jobType'], 1)  # 2 part time jobs
    #     doFilter(job, "one of my", "permanent", 1)  # 2 part time jobs

    # def test_0406_getNewestJobBySource(self):
    #     job = self.JOBS[3]
    #     source = job["source"]
    #     jobFound = self.jobsModule.getNewestBySource(source)
    #     self.assertEqual(jobFound["id"], job["id"])

    # def test_0407_getNewestJobBySource(self):
    #     job = self.JOBS[1]
    #     jobFound = self.jobsModule.getNewest(filtering={})
    #     self.assertEqual(jobFound["id"], job["id"])

    # # @TODO: delete method test

    # def test_0406_getByIdsFilter(self):
    #     ids = [self.JOBS[0]["id"], self.JOBS[1]["id"]]
    #     filtering = {
    #         "ids": ids
    #     }
    #     jobsFound = self.jobsModule.get(filtering=filtering)
    #     self.assertEqual(2, jobsFound.count())


unittest.main()
