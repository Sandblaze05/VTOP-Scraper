var data = '_csrf=' + $('input[name="_csrf"]').val() + '&semesterSubId=' + semId + '&authorizedID=' + $('#authorizedIDX').val();
var response = {
    courses: []
};

$.ajax({
    type: 'POST',
    url: 'processViewTimeTable',
    data: data,
    async: false,
    success: function (res) {
        var doc = new DOMParser().parseFromString(res, 'text/html');
        if (!doc.getElementById('studentDetailsList')) {
            return;
        }
        var table = doc.getElementById('studentDetailsList').getElementsByTagName('table')[0];
        var headings = table.getElementsByTagName('th');
        var offset = headings[0].innerText.toLowerCase().includes('invoice') ? -1 : 0;
        var courseIndex, creditsIndex, slotVenueIndex, facultyIndex;

        for (var i = 0; i < headings.length; ++i) {
            var heading = headings[i].innerText.toLowerCase();
            if (heading == 'course') {
                courseIndex = i;
            } else if (heading == 'l t p j c') {
                creditsIndex = i;
            } else if (heading.includes('slot')) {
                slotVenueIndex = i;
            } else if (heading.includes('faculty')) {
                facultyIndex = i;
            }
        }

        var cells = table.getElementsByTagName('td');
        while (courseIndex < cells.length && creditsIndex < cells.length && slotVenueIndex < cells.length && facultyIndex < cells.length) {
            var course = {};
            var rawCourse = cells[courseIndex + offset].innerText.replace(/\t/g, '').replace(/\n/g, ' ');
            var rawCourseType = rawCourse.split('(').slice(-1)[0].toLowerCase();
            var rawCredits = cells[creditsIndex + offset].innerText.replace(/\t/g, '').replace(/\n/g, ' ').trim().split(' ');
            var rawSlotVenue = cells[slotVenueIndex + offset].innerText.replace(/\t/g, '').replace(/\n/g, '').split('-');
            var rawFaculty = cells[facultyIndex + offset].innerText.replace(/\t/g, '').replace(/\n/g, '').split('-');

            course.code = rawCourse.split('-')[0].trim();
            course.title = rawCourse.split('-').slice(1).join('-').split('(')[0].trim();
            course.type = (rawCourseType.includes('lab')) ? 'lab' : ((rawCourseType.includes('project')) ? 'project' : 'theory');
            course.credits = parseInt(rawCredits[rawCredits.length - 1]) || 0;
            course.slots = rawSlotVenue[0].trim().split('+');
            course.venue = rawSlotVenue.slice(1, rawSlotVenue.length).join(' - ').trim();
            course.faculty = rawFaculty[0].trim();
            response.courses.push(course);

            courseIndex += headings.length + offset;
            creditsIndex += headings.length + offset;
            slotVenueIndex += headings.length + offset;
            facultyIndex += headings.length + offset;
        }
    }
});
return JSON.stringify(response);
