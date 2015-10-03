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
        self.jobsIndex = defaultdict(dict)

    @classmethod
    def locationsMatch(cls, location1, location2):
        l1 = location1
        l2 = location2

        l1State = l1.get("state", False)
        l2State = l2.get("state", False)
        l1County = l1["country"].lower()
        l2County = l2["country"].lower()
        l1City = l1["city"].lower()
        l2City = l2["city"].lower()

        if l1County != l2County or l1City != l2City:
            return False

        if l1State and l2State and l1State != l2State:
            return False

        return True

    @classmethod
    def extractWords(cls, text):
        return text.lower().split(cls.tokenSplitter)

    def startIndexing(self):
        for position, job in enumerate(self.jobs):
            words = self.extractWords(job["title"] + " " + job["description"])
            for word in words:
                wordIndex = self.jobsIndex[word]
                wordIndex[position] = True
                self.jobsIndex[word] = wordIndex

    def findMatchingJobs(self, alarm):
        haystack = self.jobsIndex.keys()
        found = []
        for keyword in alarm["keywords"]:
            # print "keyword: ", keyword
            for word in haystack:
                # print keyword.lower(), word
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
                job = self.jobs[position]
                if self.locationsMatch(job["location"], alarm["location"]):
                    machings.insert(job, alarm)
        return machings
