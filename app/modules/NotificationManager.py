# -*- coding: utf8 -*-

import arrow
from collections import defaultdict

from accounts import Accounts
from matcher import Matcher
from GCMClient import GCMClient
from app.libraries import loggerFactory

logger = loggerFactory.get()


class NotificationManager(object):
    """
    This class is responsible for the works relevant to the alarm
    notifications and can be regarded as the entry point of notification layer;
    that is, other modules will interact with only this class.
    """
    def __init__(self, config):
        super(NotificationManager, self).__init__()
        self.matcher = Matcher()
        self.publisher = GCMClient({})
        self.accountCollection = Accounts(config)

    def setJobs(self, jobsIterator):
        self.matcher.setJobs(jobsIterator)
        return self

    def start(self):
        accounts = self.accountCollection.get()
        for account in accounts:
            try:
                matches = self.matcher.match(account["alarms"])
                if matches.count() == 0:
                    continue

                notification = Notification(matches)
                gcmID = account["gcmId"]
                self.publisher.publish(gcmID, notification)

                logger.info("notification - gcm: {} {}".format(
                    gcmID, repr(notification)))

                ntfStatus = notification.genNotificationID()
                self.accountCollection.saveNotifiactionStatus(
                    account["_id"], ntfStatus)

                logger.debug("saved notification status: {}".format(
                    ntfStatus))
            except Exception, e:
                emsg = "notification builder error. accountId <{}>".format
                logger.exception(emsg(account["_id"]))


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
            "createdAt": self.createdAt.naive,
            "jobIDs": self.matches.getJobIDs(),
            "matchingCount": self.matches.count()
        }
