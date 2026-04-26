
$(document).on("click", ".custom-select-trigger", function(e) {
    e.preventDefault();
    e.stopPropagation();

    // Tìm cái div cha có class .custom-select (nhưng không phải thẻ select)
    const $visualSelect = $(this).closest("div.custom-select");

    // Đóng tất cả các cái khác
    $("div.custom-select").not($visualSelect).removeClass("opened");

    // Toggle class cho cái div giả
    $visualSelect.toggleClass("opened");
});

$(document).on("click", ".custom-option", function(e) {
    e.stopPropagation();
    const $option = $(this);
    const $customSelect = $option.closest(".custom-select");
    const $wrapper = $option.closest(".custom-select-wrapper");
    const val = $option.data("value");
    const text = $option.text();

    // 1. Cập nhật select thật (ẩn)
    $wrapper.find("select").val(val).trigger('change');

    // 2. Cập nhật giao diện giả
    $option.siblings().removeClass("selection");
    $option.addClass("selection");
    $customSelect.find(".custom-select-trigger").text(text);
    $customSelect.removeClass("opened");
});

// Click ra ngoài thì đóng
$(document).on("click", function() {
    $(".custom-select").removeClass("opened");
});

function initCustomSelectForElement($element) {
    $element.each(function() {
        var $this = $(this);
        if ($this.parent().hasClass("custom-select-wrapper")) return;

        var placeholder = $this.attr("placeholder") || "-- Chọn --";

        // SỬA TẠI ĐÂY: Thêm 1 class định danh riêng cho div giả, ví dụ: 'custom-select-visual'
        var template = '<div class="custom-select custom-select-visual">';
        template += '<span class="custom-select-trigger">' + placeholder + '</span>';
        template += '<div class="custom-options">';

        $this.find("option").each(function() {
            var val = $(this).val();
            if (val !== "") {
                template += '<span class="custom-option" data-value="' + val + '">' + $(this).html() + '</span>';
            }
        });
        template += '</div></div>';

        $this.wrap('<div class="custom-select-wrapper"></div>');
        $this.hide(); // Ẩn cái select thật đi
        $this.after(template);
    });
}

$(document).ready(function() {
    // Gọi hàm cho tất cả các thẻ select có class .my-select
    initCustomSelectForElement($("select.card-search-input"));
});