
var $list = $("#jobs tbody")
var $toolbar = $(".listToolbar")

function loadJobEntries($container, dateRange) {

    var timestamp = new Date().getTime() / 1000;
    var dateStart, dateEnd;

    if (dateRange == "yesterday") {
        dateStart = timestamp - 3600 * 24 * 2;
        dateEnd = timestamp - 3600 * 24;
    } else if (dateRange == "week") {
        dateStart = timestamp - 3600 * 24 * 7;
    } else {
        dateStart = timestamp - 3600 * 24;
        dateEnd = timestamp;
    }

    $container.html("loading...")

    $.ajax({
        url: "/api/v1/admin/jobs",
        data: {"dateStart": dateStart, "dateEnd": dateEnd}
    }).success(function(data){
        if (!data.status || data.status.code != 20) {
            console.error("cannot fetch job entries." + data)
            $container.html("cannot fetch job entries: " + data)
            return
        }

        console.debug(data)
        $container.html("")
        var jobs = data.data.jobs;
        $("#jobCount").html(jobs.length + " jobs")
        for(var i = 0; i < jobs.length; i++)
            renderJobEntry($container, jobs[i])
        bindJobEntries($container)
    })
}


function renderJobEntry($container, job) {
    var template = $('#template_job_entry').html();
    Mustache.parse(template);

    job.locationStr= function(){
        return this.location.country + " / " +
            this.location.state + " / " +this.location.city;
    };

    job.sourceUrl= function(){
        if (this.source == "user")
            return
        url = "http://londonjobs.co.uk/" + this.meta.url;
        return "<a href='" + url + "' target='_blank' >go</a>"
    }

    var rendered = Mustache.render(template, job)
    $container.append(rendered)
}

function bindJobEntries($container) {
    $list.find("button.toggleDetail").click(function() {
        // tr > td > button
        // tr.detail
        $(this).toggleClass("expanded");
        $(this).parent().parent().next().toggle();
    })

    $list.find(".selection input").click(function() {
        if ($container.find(".selection input:checked").length == 0)
            $(".listToolbar .actions .delete").hide()
        else
            $(".listToolbar .actions .delete").show()
    })
}

function bindListToolbar() {
    $toolbar.find(".actions .delete").click(deleteJobs)
}

function getSelectedJobs() {
    return $list.find(".selection input:checked")
}

function deleteJobs() {
    var $jobs = getSelectedJobs();
    if (!confirm("Are you sure to delete " + $jobs.length + " jobs PERMANENTLY?"))
        return;

    $jobs.each(function() {
        var $input = $(this)
        var id = $input.val();
        $input.closest('tr').addClass("deleted")

        $.ajax({
            method: "DELETE",
            url: "/api/v1/admin/job/" + id,
        }).success(function(data){
            console.log("deletion job " + id + ": " + data.status.message)
            if (data.status.code == 20)
                removeJobFromList($input.closest('tr'))
        }).fail(function(e){
            console.error("cannot delete job " + id + ". ")
            console.error(e)
        }).always(function(){
            $input.closest('tr').removeClass("deleted")
        })
    })
    $(".listToolbar .actions .delete").hide()
}

function removeJobFromList(tr) {
    $(tr).next().remove();
    $(tr).remove();
}

function plotTimeseriesForNewJobs($container) {
    var m = moment()
    var interval = "hourly"
    var dateEnd = m.format("YYYY-MM-DD HH:mm:ss")
    var dateStart = m.subtract(3, "months").format("YYYY-MM-DD")

    $.getJSON('/api/v1/admin/jobs/analysis/newJobs/?interval='
        + interval + '&dateStart=' + dateStart + '&dateEnd=' + dateEnd, function (data) {

        timeseries = data.data.timeseries
        formated = []
        for(var i in timeseries)
            formated.push([
                new Date(i + ":00:00").getTime(),
                timeseries[i]
            ])


        $($container).highcharts('StockChart', {
            rangeSelector : {
                buttons: [
                    {
                        type: "day",
                        count: 1,
                        text: "1d"
                    },
                    {
                        type: "day",
                        count: 3,
                        text: "3d"
                    },
                    {
                        type: "week",
                        count: 1,
                        text: "1w"
                    },
                    {
                        type: "month",
                        count: 1,
                        text: "1m"
                    }
                ],
                selected : 0
            },
            title : {
                text : 'New Job Entries'
            },
            series : [{
                name : 'jobs',
                data : formated,
                tooltip: {
                    valueDecimals: 0
                }
            }]
        });
    });
}