# -*- coding: utf8 -*-
import arrow
from app.libraries import loggerFactory
from app.libraries.mongodb import getDb
from app.modules.jobs import generateQuery


def renameID(job):
    job["id"] = job["_id"]
    del job["_id"]
    return job


class JobsAnalyzer(object):
    """This module handles operations related to job entries"""

    def __init__(self, config):
        super(JobsAnalyzer, self).__init__()
        self.db = getDb()
        self.jobsStorage= self.db["jobs"]
        self.logger = loggerFactory.get()

    def getFrequency(self, interval, filtering):
        query = generateQuery(filtering)

        groupId = {
            "day": {"$dayOfMonth": "$date"},
            "month": {"$month": "$date"},
            "year": {"$year": "$date"}}

        result = self.jobsStorage.aggregate([
            {"$match": query},
            {"$group": {
                "_id": groupId,
                "date": {"$first": "$date"},
                "count": {"$sum": 1}
            }}
        ])

        if interval == "hourly":
            intervalSeconds = 3600
            dateFormat = 'YYYY-MM-DD HH'
            groupId["hour"] = {"$hour": "$date"}
        else:
            intervalSeconds = 3600 * 24
            dateFormat = 'YYYY-MM-DD'

        # creating a timeserie list filled with zeroes.
        timeseriesRange = range(
            arrow.get(filtering["dateStart"]).timestamp,
            arrow.get(filtering["dateEnd"]).timestamp,
            intervalSeconds
        )
        timeseries = {}
        for i in timeseriesRange:
            timeseries[arrow.get(i).format(dateFormat)] = 0

        for i in result:
            timeseries[arrow.get(i["date"]).format(dateFormat)] = i["count"]

        return timeseries



