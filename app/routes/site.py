# -*- coding: utf8 -*-
from app.modules.jobs import Jobs
from flask import Blueprint, render_template, request
import arrow


def getBlueprint(config):
    app = Blueprint("site", __name__)

    def getJobList(filtering, active_page):
        jobs = Jobs(config).get(filtering)
        return render_template(
            "jobs.html",
            filtering=filtering, jobs=jobs, jobsCount=jobs.count())

    @app.route("/jobs/")
    def index():
        filtering = {
            "title": request.args.get("keyword", ""),
            "description": request.args.get("keyword", ""),
            "location": {
                "country": "united kingdom",
                "city": request.args.get("location", "london")
            }
        }
        return getJobList(filtering, "today")

    @app.route("/jobs/<jobId>/", defaults={"title": None})
    @app.route("/jobs/<jobId>/<path:title>")
    def getJobDetail(jobId, title):
        job = Jobs(config).getOne(jobId)
        return render_template("job.html", job=job)

    return app
