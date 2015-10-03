# -*- coding: utf8 -*-
from collections import defaultdict


class Matchings(object):
    """Represents a job and alarm matching and generally used by
    the class Matcher"""
    def __init__(self):
        super(Matchings, self).__init__()
        self.matches = {}

    def insert(self, job, alarm):
        matching = self.matches.get(job["id"], {"alarms": [], "job": None})
        matching["alarms"].append(alarm)
        matching["job"] = job
        self.matches[job["id"]] = matching
        return self

    def getJobs(self):
        return (i["job"] for i in self.matches.values())

    def getAlarms(self):
        """
        return list of matching unique alarms
        """
        return {i["id"]: i for a in self.matches.values()
                for i in a["alarms"]}.values()

    def getJobIDs(self):
        return [str(i["id"]) for i in self.getJobs()]

    def getAlarmIDs(self):
        return [str(i["id"]) for i in self.getAlarms()]

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
        machings = Matchings()
        for alarm in alarms:
            matchingJobPositions = self.findMatchingJobs(alarm)
            for position in matchingJobPositions:
                matchingJob = self.jobs[position]
                machings.insert(matchingJob, alarm)
        return machings
