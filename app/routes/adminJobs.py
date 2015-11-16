# -*- coding: utf8 -*-
import arrow
from app.modules.adminAuth import requires_admin
from app.modules.jobs import Jobs
from app.modules.jobsAnalyzer import JobsAnalyzer
from flask import Blueprint, jsonify, request
from app.libraries import response


def getBlueprint(config):
    app = Blueprint('adminJobs', __name__)

    @app.route("/admin/jobs/analysis/newJobs/")
    @requires_admin
    def getAnalysis():
        # TODO: adds to api documentation
        dateStart = request.args.get("dateStart")
        dateEnd = request.args.get("dateEnd")
        interval = request.args.get("interval")
        filtering={
            "dateStart": arrow.get(dateStart).naive,
            "dateEnd": arrow.get(dateEnd).naive
        }
        result = JobsAnalyzer(config).getFrequency(interval, filtering)

        filtering["interval"] = interval;
        return jsonify(response.make(20, {
            "filtering": filtering,
            "timeseries": result}).__json__())

    @app.route("/admin/jobs")
    @requires_admin
    def getJobs():
        filtering = {
            "dateStart": request.args.get("dateStart", None),
            "dateEnd": request.args.get("dateEnd", None),
            "title": request.args.get("keyword", None),
            "description": request.args.get("keyword", None),
            "jobType": request.args.get("jobType", None)
        }
        jobs = list(Jobs(config).get(filtering=filtering, length=10000))
        return jsonify(response.make(20, {
            "filter": filtering,
            "jobs": jobs}).__json__())

    @app.route("/admin/job/<jobId>", methods=["DELETE"])
    @requires_admin
    def delete(jobId):
        Jobs(config).delete(jobId)
        return jsonify(response.make(20, {
            "job": {"id": jobId}}).__json__())

    return app


