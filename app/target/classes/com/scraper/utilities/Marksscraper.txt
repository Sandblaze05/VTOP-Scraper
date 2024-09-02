var data = 'semesterSubId=' + semId + '&authorizedID=' + $('#authorizedIDX').val() + '&_csrf=' + $('input[name="_csrf"]').val();
var response = {
    marks: []
};

$.ajax({
    type: 'POST',
    url: 'examinations/doStudentMarkView',
    data: data,
    async: false,
    success: function (res) {
        if (res.toLowerCase().includes('no data found')) {
            return;
        }
        var doc = new DOMParser().parseFromString(res, 'text/html');
        var table = doc.getElementById('fixedTableContainer');
        var rows = table.getElementsByTagName('tr');
        var headings = rows[0].getElementsByTagName('td');
        var courseTypeIndex, slotIndex;

        for (var i = 0; i < headings.length; ++i) {
            var heading = headings[i].innerText.toLowerCase();
            if (heading.includes('course') && heading.includes('type')) {
                courseTypeIndex = i;
            } else if (heading.includes('slot')) {
                slotIndex = i;
            }
        }

        for (var i = 1; i < rows.length; ++i) {
            var rawCourseType = rows[i].getElementsByTagName('td')[courseTypeIndex].innerText.trim().toLowerCase();
            var courseType = (rawCourseType.includes('lab')) ? 'lab' : ((rawCourseType.includes('project')) ? 'project' : 'theory');
            var slot = rows[i++].getElementsByTagName('td')[slotIndex].innerText.split('+')[0].trim();
            var innerTable = rows[i].getElementsByTagName('table')[0];
            var innerRows = innerTable.getElementsByTagName('tr');
            var innerHeadings = innerRows[0].getElementsByTagName('td');
            var titleIndex=1; var scoreIndex=5; var maxScoreIndex=2;var weightageIndex=6;var maxWeightageIndex=3;var averageIndex=0;var statusIndex=4;

            var innerCells = innerTable.getElementsByTagName('td');
            while (titleIndex < innerCells.length && scoreIndex < innerCells.length && maxScoreIndex < innerCells.length && weightageIndex < innerCells.length && maxWeightageIndex < innerCells.length && averageIndex < innerCells.length && statusIndex < innerCells.length) {
                var mark = {};
                mark.slot = slot;
                mark.course_type = courseType;
                mark.title = innerCells[titleIndex].innerText.trim();
                mark.score = parseFloat(innerCells[scoreIndex].innerText) || 0;
                mark.max_score = parseFloat(innerCells[maxScoreIndex].innerText) || null;
                mark.weightage = parseFloat(innerCells[weightageIndex].innerText) || 0;
                mark.max_weightage = parseFloat(innerCells[maxWeightageIndex].innerText) || null;
                mark.average = parseFloat(innerCells[averageIndex].innerText) || null;
                mark.status = innerCells[statusIndex].innerText.trim();
                response.marks.push(mark);

                titleIndex += innerHeadings.length;
                scoreIndex += innerHeadings.length;
                maxScoreIndex += innerHeadings.length;
                weightageIndex += innerHeadings.length;
                maxWeightageIndex += innerHeadings.length;
                averageIndex += innerHeadings.length;
                statusIndex += innerHeadings.length;
            }
            i += innerRows.length;
        }
    }
});
return JSON.stringify(response);