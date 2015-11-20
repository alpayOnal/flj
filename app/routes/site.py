# -*- coding: utf8 -*-
from app.modules.jobs import Jobs
from flask import Blueprint, render_template


def getBlueprint(config):
    app = Blueprint("site", __name__)

    @app.route("/today")
    def index():
        jobs = Jobs(config).get(filtering={})
        return render_template(
            "jobs.html", jobs=jobs,
            active_page="today", jobsCount=jobs.count())

    @app.route("/yesterday")
    def yesterday():
        jobs = Jobs(config).get(filtering={})
        return render_template(
            "jobs.html", jobs=jobs,
            jobsCount=jobs.count(), active_page="yesterday")

    @app.route("/thisweek")
    def thisweek():
        jobs = Jobs(config).get(filtering={})
        return render_template("jobs.html", jobs=jobs, active_page="thisweek")

    @app.route("/job/<jobId>/", defaults={"title": None})
    @app.route("/job/<jobId>/<path:title>")
    def getJobDetail(jobId, title):
        job = Jobs(config).getOne(jobId)
        return render_template("job.html", job=job)

    return app
