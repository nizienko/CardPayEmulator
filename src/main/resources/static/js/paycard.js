var pay = function() {
    var cardNumber = document.getElementById('cardNumber').value;
    var month = document.getElementById('validUntilMonth').value;
    var year = document.getElementById('validUntilYear').value;
    var holder = document.getElementById('holder').value;
    var cvc = document.getElementById('cvc').value;
    var sum = document.getElementById('sum').value;
    showContent("Подождите...");
    document.getElementById('pay_form').style.visibility='hidden';
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
                document.getElementById('one_more').style.visibility='visible';
            }
            else {
                showContent("Ошибка: " + response.message);
                document.getElementById('one_more').style.visibility='visible';
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            showContent("Ошибка: " + xhr.statusText + "<br>" + xhr.responseText);
            document.getElementById('one_more').style.visibility='visible';
        }
    });
}

var showContent = function(content) {
    $('#content').html(content);
}

var oneMore = function(content) {
    $('#content').html("");
    document.getElementById('pay_form').style.visibility='visible';
    document.getElementById('one_more').style.visibility='hidden';
}