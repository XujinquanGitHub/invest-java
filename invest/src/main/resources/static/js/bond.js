function loadBondData() {
    var dataPost = [];
    $("#flex_cb").find("tbody tr").each(function() {
        var divNone = $(this).css("display");
        if (!divNone || divNone == "none") {
            return;
        }
        var id = $(this).attr("id");
        var sprice = $(this).find("td[data-name=sprice]").text();
        dataPost.push({
            "id": id,
            "p": sprice
        });
    });

    $.ajax({
        async: true,
        url: '/bond/allBond1',
        type: 'POST',
        data: JSON.stringify({"para": dataPost}),
        dataType: 'json',
        timeout: 30000,
        success: function(data) {
            for (let index = 0; index < data.length; index++) {
                const element = data[index];
                var thatObj = $("#" + element.code);
                thatObj.find("td[data-name=bond_value]").text(element.pure_value);
                thatObj.find("td[data-name=_option_value]").text(element.bsmValue);
                thatObj.find("td[data-name=volatility_rate]").text(element.bd);
                var reasonableValue = element.pure_value + element.bsmValue;
                var price = thatObj.find("td[data-name=price]").text();
                var dtPrecent = ((price / reasonableValue) - 1) * 100;
                dtPrecent = dtPrecent.toFixed(2);
                thatObj.find("td[data-name=fund_rt]").text(dtPrecent + "%");
                thatObj.find("td[data-name=fund_rt]").attr("title", "合理价值：" + reasonableValue);
                thatObj.find("td[data-name=rating_cd]").attr("title", "yy评级：" + element.yy);
            }
        },
        error: function(xhr, status, error) {
            console.error("加载债券数据失败:", error);
        }
    });
} 