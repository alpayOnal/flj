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
    })
}


function renderJobEntry($container, job) {
    var template = $('#template_job_entry').html();
    Mustache.parse(template);
    var rendered = Mustache.render(template, job)
    $container.append(rendered)
}