var data = '_csrf=' + $('input[name="_csrf"]').val() + '&semesterSubId=' + semId + '&authorizedID=' + $('#authorizedIDX').val();

var response = {
    lab: [],
    theory: []
};

$.ajax({
    type: 'POST',
    url: 'processViewTimeTable',
    data: data,
    async: false,
    success: function(res) {
        var doc = new DOMParser().parseFromString(res, 'text/html');
        var spans = doc.getElementById('getStudentDetails').getElementsByTagName('span');

        if (spans[0].innerText.toLowerCase().includes('no record(s) found')) {
            return;
        }

        var cells = doc.getElementById('timeTableStyle').getElementsByTagName('td');
        var key, type;

        for (var i = 0, j = 0; i < cells.length; ++i) {
            var content = cells[i].innerText.toUpperCase();

            if (content.includes('THEORY')) {
                type = 'theory';
                j = 0;
                continue;
            } else if (content.includes('LAB')) {
                type = 'lab';
                j = 0;
                continue;
            } else if (content.includes('START')) {
                key = 'start';
                continue;
            } else if (content.includes('END')) {
                key = 'end';
                continue;
            } else if (content.includes('SUN')) {
                key = 'sunday';
                continue;
            } else if (content.includes('MON')) {
                key = 'monday';
                continue;
            } else if (content.includes('TUE')) {
                key = 'tuesday';
                continue;
            } else if (content.includes('WED')) {
                key = 'wednesday';
                continue;
            } else if (content.includes('THU')) {
                key = 'thursday';
                continue;
            } else if (content.includes('FRI')) {
                key = 'friday';
                continue;
            } else if (content.includes('SAT')) {
                key = 'saturday';
                continue;
            } else if (content.includes('LUNCH')) {
                continue;
            }

            if (key == 'start') {
                response[type].push({ start_time: content.trim() });
            } else if (key == 'end') {
                response[type][j++].end_time = content.trim();
            } else if (cells[i].bgColor == '#CCFF33') {
                response[type][j++][key] = content.split('-')[0].trim();
            } else {
                response[type][j++][key] = null;
            }
        }
    }
});
return JSON.stringify(response);