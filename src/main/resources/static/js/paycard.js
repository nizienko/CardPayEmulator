var pay = function() {
    var cardNumber = document.getElementById('cardNumber').value;
    var month = document.getElementById('validUntilMonth').value;
    var year = document.getElementById('validUntilYear').value;
    var holder = document.getElementById('holder').value;
    var cvc = document.getElementById('cvc').value;
    var sum = document.getElementById('sum').value;
    showContent("Подождите...");
    $.ajax({
        type: "POST",
        cache: false,
        url: '/card/pay',
        data: {
            'cardNumber':cardNumber,
            'month':month,
            'year':year,
            'holder':holder,
            'cvc':cvc,
            'sum':sum
        },
        success: function (response) {
            if (response.result == "success")
            {
                showContent(response.data.message);
            }
            else {
                showContent("Ошибка");
                alert(response.message);
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            alert(xhr.statusText + '\n' + xhr.responseText);
        }
    });
}

var showContent = function(content) {
    $('#content').html(content);
}