var authorizedID = $('#authorizedIDX').val();
var csrfToken = $('input[name="_csrf"]').val();
var nocache = new Date().getTime();

var data = 'verifyMenu=true&authorizedID=' + authorizedID + '&_csrf=' + csrfToken + '&nocache=' + nocache;

var response = {};

$.ajax({
    type: 'POST',
    url: 'examinations/examGradeView/StudentGradeHistory',
    data: data,
    async: false,
    success: function(res) {
        var doc = new DOMParser().parseFromString(res, 'text/html');
        var tables = doc.getElementsByTagName('table');

        for (var i = tables.length - 1; i >= 0; --i) {
            var headings = tables[i].getElementsByTagName('tr')[0].getElementsByTagName('td');

            if (headings[0].innerText.toLowerCase().includes('credits')) {
                var creditsIndex, cgpaIndex;

                for (var j = 0; j < headings.length; ++j) {
                    var heading = headings[j].innerText.toLowerCase();

                    if (heading.includes('earned')) {
                        creditsIndex = j + headings.length;
                    } else if (heading.includes('cgpa')) {
                        cgpaIndex = j + headings.length;
                    }
                }

                var cells = tables[i].getElementsByTagName('td');

                response.cgpa = parseFloat(cells[cgpaIndex].innerText) || 0;
                response.total_credits = parseFloat(cells[creditsIndex].innerText) || 0;
                break;
            }
        }
    }
});

return JSON.stringify(response);
