var data = '_csrf=' + $('input[name="_csrf"]').val() + '&semesterSubId=' + semId + '&authorizedID=' + $('#authorizedIDX').val();

var response = {
    attendance: []
};

$.ajax({
    type: 'POST',
    url: 'processViewStudentAttendance',
    data: data,
    async: false,
    success: function (res) {
        var doc = new DOMParser().parseFromString(res, 'text/html');
        var table = doc.getElementById('getStudentDetails');
        var headings = table.getElementsByTagName('tr');

        var courseTypeIndex=2; var slotIndex=3; var attendedIndex=5; var totalIndex=6; var percentageIndex=7;

        var cells = table.getElementsByTagName('td');

        while (courseTypeIndex < cells.length && slotIndex < cells.length && attendedIndex < cells.length && totalIndex < cells.length && percentageIndex < cells.length) {
            var attendanceObject = {};

            attendanceObject.course_type = cells[courseTypeIndex].innerText.trim();
            attendanceObject.slot = cells[slotIndex].innerText.trim().split('+')[0].trim();
            attendanceObject.attended = parseInt(cells[attendedIndex].innerText.trim()) || 0;
            attendanceObject.total = parseInt(cells[totalIndex].innerText.trim()) || 0;
            attendanceObject.percentage = parseInt(cells[percentageIndex].innerText.trim()) || 0;

            response.attendance.push(attendanceObject);

            courseTypeIndex += 10;
            slotIndex += 10;
            attendedIndex += 10;
            totalIndex += 10;
            percentageIndex += 10;
        }
    }
});
return JSON.stringify(response);