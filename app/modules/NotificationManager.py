# -*- coding: utf8 -*-
# from app.libraries import loggerFactory
# from app.libraries.mongodb import getDb
# from app.modules.errors import (NotFoundException,
#                                 WrongArgumentException)

import arrow

from collections import defaultdict
from matcher import Matcher, Matches
from GCMClient import GCMClient


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
        self.publisher = GCMClient({})
        # self.accountCollection = Accounts()

    def setJobs(self, jobsIterator):
        self.matcher.setJobs(jobsIterator)
        return self

    def start(self):
        # accounts = [{
        #     "id": 1,
        #     "alarms": [
        #         {
        #             "id": "jrt9",
        #             "keywords": ["armut"]
        #         },
        #         {
        #             "id": "mke3",
        #             "keywords": ["elma"]
        #         }
        #     ]
        # }, {
        #     "id": 2,
        #     "alarms": [
        #         {
        #             "id": "a312",
        #             "keywords": ["kav"]
        #         },
        #         {
        #             "id": "b598",
        #             "keywords": ["elma"]
        #         }
        #     ]
        # }]
        accounts = self.accountCollection.get()
        for account in accounts:
            try:
                matches = self.matcher.match(account["alarms"])
                notification = Notification(matches)
                # print "account: ", account["id"]
                # print notification.getTitle()
                # print notification
                self.publisher.publish("gcm1", notification)

                ntfStatus = notification.genNotificationID()
                self.accountCollection.saveNotifiactionStatus(ntfStatus)
            except Exception, e:
                raise e
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
        self.createdAt = arrow.utcnow()

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

    def __repr__(self):
        alarmIDs = self.matches.getAlarmIDs()
        jobIDs = self.matches.getJobIDs()
        return "alarmID: {alarmIDs} jobIDs: {jobIDs}".format(
            alarmIDs=", ".join(alarmIDs), jobIDs=", ".join(jobIDs))

    def genNotificationID(self):
        return {
            "created_at": self.createdAt,
            "latestJobId": self.matches.getLatesJobID(),
            "matchingCount": self.matches.count()
        }


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
