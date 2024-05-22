$(document).ready(function() {
    var csrfToken = $('meta[name="csrf-token"]').attr('content');
    var csrfHeader = $('meta[name="csrf-param"]').attr('content');

    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (csrfToken && csrfHeader) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        }
    });
});
