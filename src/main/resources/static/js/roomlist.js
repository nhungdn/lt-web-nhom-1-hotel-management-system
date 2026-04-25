function renderBookingList(data) {
  const tbody = document.querySelector("#roomTable tbody");
  const template = document.getElementById("booking-template");

  if (!renderBookingList.templateHtml && template) {
    renderBookingList.templateHtml = template.innerHTML;
  }

  if (!renderBookingList.templateHtml) {
    console.error("Không tìm thấy template booking để render bảng.");
    return;
  }

  tbody.innerHTML = ""; // Xóa trắng bảng trước khi render

  data.forEach((booking) => {
    const rowTemplate = document.createElement("template");
    rowTemplate.innerHTML = renderBookingList.templateHtml.trim();
    const clone = rowTemplate.content.cloneNode(true);
    const tr = clone.querySelector("tr");

    // Đổ dữ liệu vào hàng chính
    tr.querySelector(".bid").textContent = `#${booking.bookingId}`;
    tr.querySelector(".name").textContent = booking.customerName;
    tr.querySelector(".id-card").textContent = `ID: ${booking.customerIDCard}`;
    tr.querySelector(".phone").textContent = booking.customerPhone;
    tr.querySelector(".email").textContent = booking.customerEmail;

    const statusBadge = tr.querySelector(".status-badge");
    renderStatusBadge(booking, statusBadge);

    const detailsBtn = tr.querySelector(".details-btn");
    if (detailsBtn) {
      detailsBtn.addEventListener("click", function (event) {
        event.stopPropagation();
        toggleDetailRow(tr, booking);
      });
    }

    // Gán sự kiện click để hiện detail
    tr.addEventListener("click", function (event) {
      if (event.target.closest("button")) {
        return;
      }
      toggleDetailRow(this, booking);
    });

    tbody.appendChild(tr);
  });
}

function getBookingRoomNumbers(booking) {
  if (!booking?.details || booking.details.length === 0) {
    return [];
  }

  return booking.details
    .map((detail) => String(detail.roomNumber ?? "").trim())
    .filter((room) => room.length > 0);
}

function initRoomSearch(data) {
  const input = document.getElementById("roomSearchInput");
  if (!input) {
    return;
  }

  const filterByRoomNumber = () => {
    const keyword = input.value.trim().toLowerCase();
    if (!keyword) {
      renderBookingList(data);
      return;
    }

    const filtered = data.filter((booking) => {
      const rooms = getBookingRoomNumbers(booking);
      return rooms.some((room) => room.toLowerCase().includes(keyword));
    });

    renderBookingList(filtered);
  };

  input.addEventListener("input", filterByRoomNumber);
}

function toggleDetailRow(clickedRow, booking) {
  const nextRow = clickedRow.nextElementSibling;

  // 1. Nếu hàng tiếp theo chính là hàng detail của nó -> Đóng lại
  if (nextRow && nextRow.classList.contains("detail-row")) {
    nextRow.remove();
    clickedRow.classList.remove("active-row");
    return;
  }

  // 2. Đóng TẤT CẢ các hàng detail khác đang mở và xóa class active
  document.querySelectorAll(".detail-row").forEach((row) => row.remove());
  document
    .querySelectorAll(".active-row")
    .forEach((row) => row.classList.remove("active-row"));

  // 3. Tạo hàng detail mới
  const template = document.getElementById("detail-template");
  const clone = template.content.cloneNode(true);

  // Tạo một hàng tr mới để chứa nội dung từ template
  const detailRow = document.createElement("tr");
  detailRow.classList.add("detail-row");

  // Lấy nội dung bên trong template (là các thẻ td) bỏ vào tr mới
  detailRow.innerHTML = clone.querySelector("td").outerHTML;

  // 4. Đổ dữ liệu vào bảng con
  const detailBody = detailRow.querySelector(".detail-body");
  if (booking.details && booking.details.length > 0) {
    booking.details.forEach((d) => {
      const badgeClass = getStatusClass(d.status);
      const rowHtml = `
                <tr>
                    <td class="bdid">${d.bookingDetailId}</td>
                    <td><span class="status-badge ${badgeClass}">${d.status}</span></td>
                    <td><strong>${d.roomNumber || "N/A"}</strong></td>
                    <td>${formatDateTime(d.checkIn)}</td>
                    <td>${formatDateTime(d.checkOut)}</td>
                    <td>
                        <button data-booking-id="${booking.bookingId}" data-detail-id="${d.bookingDetailId}" 
                        onclick=renderEditForm(this) class="card-btn action">Edit</button>
                      <button data-booking-id="${booking.bookingId}" data-detail-id="${d.bookingDetailId}"
                        onclick="openPaymentPopupByDetail(this)" type="button" class="card-btn action">Thanh toán</button>
                    </td>
                </tr>
            `;
      detailBody.insertAdjacentHTML("beforeend", rowHtml);
    });
  } else {
    detailBody.innerHTML =
      '<tr><td colspan="5" style="text-align:center;">Không có chi tiết phòng</td></tr>';
  }

  // 5. Hiển thị
  clickedRow.classList.add("active-row");
  clickedRow.after(detailRow);
}

function renderStatusBadge(booking, statusBadge) {
  // Xóa nội dung cũ và reset class
  statusBadge.innerHTML = "";
  statusBadge.className = "status-container"; // Sử dụng class container để quản lý layout

  if (!booking.details || booking.details.length === 0) {
    const noneBadge = document.createElement("div");
    noneBadge.className = "status-badge gray";
    noneBadge.textContent = "No Details";
    statusBadge.appendChild(noneBadge);
    return;
  }

  // Lấy danh sách các status duy nhất
  const uniqueStatuses = [...new Set(booking.details.map((d) => d.status))];

  const statusConfig = {
    PENDING: { color: "yellow", label: "PENDING" },
    CHECKED_IN: { color: "blue", label: "CHECKED_IN" },
    COMPLETED: { color: "green", label: "COMPLETED" },
    CANCELED: { color: "red", label: "CANCELED" },
  };

  // Tạo các dòng trạng thái
  uniqueStatuses.forEach((status) => {
    const roomsForStatus = booking.details
      .filter((d) => d.status === status)
      .map((d) => d.roomNumber || "N/A")
      .join(", ");

    const config = statusConfig[status] || { color: "gray", label: status };

    // Tạo một wrapper div để ép xuống dòng
    const row = document.createElement("div");
    row.className = "status-row";

    // Tạo badge bên trong row
    const badge = document.createElement("span");
    badge.className = `status-badge ${config.color}`;
    badge.textContent = `${config.label} [${roomsForStatus}]`;

    row.appendChild(badge);
    statusBadge.appendChild(row);
  });
}

function getStatusClass(status) {
  switch (status) {
    case "PENDING":
      return "yellow";
    case "CHECKED_IN":
      return "blue";
    case "COMPLETED":
      return "green";
    case "CANCELED":
      return "red";
    default:
      return "";
  }
}

function formatDateTime(isoString) {
  if (!isoString || isoString === "-") return "-";

  const date = new Date(isoString);

  // Lấy tên Thứ bằng tiếng Việt
  const days = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
  const dayName = days[date.getDay()];

  // Format ngày/tháng/năm
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();

  return `${dayName}, ${day}/${month}/${year}`;
}

function formatMoney(amount) {
  const numeric = Number(amount || 0);
  return `${new Intl.NumberFormat("vi-VN").format(numeric)} đ`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function renderInvoiceList(listElement, invoices, isPaid) {
  if (!invoices || invoices.length === 0) {
    listElement.innerHTML = `<div class="payment-empty">${isPaid ? "Chưa có hóa đơn đã thanh toán" : "Không còn hóa đơn chưa thanh toán"}</div>`;
    return;
  }

  const html = invoices
    .map((invoice) => {
      const linesHtml = (invoice.chargeLines || [])
        .map(
          (line) => `
      <tr>
        <td>${escapeHtml(line.label)}</td>
        <td>${line.quantity ?? "-"}</td>
        <td>${formatMoney(line.unitPrice)}</td>
        <td>${formatMoney(line.lineTotal)}</td>
      </tr>
    `,
        )
        .join("");

      return `
      <div class="payment-invoice-item ${isPaid ? "paid" : "unpaid"}">
        <div class="payment-invoice-head">
          <strong>Detail #${invoice.bookingDetailId} - Phòng ${escapeHtml(invoice.roomNumber || "N/A")}</strong>
          <span>${isPaid ? "Đã thanh toán" : "Chưa thanh toán"}</span>
        </div>
        <div class="payment-invoice-meta">
          ${isPaid ? `Payment #${invoice.paymentId ?? "N/A"} | ${escapeHtml(invoice.paymentDate || "N/A")}` : `Đã trả: ${formatMoney(invoice.alreadyPaid)} | Còn lại: ${formatMoney(invoice.remaining)}`}
        </div>
        <table class="payment-detail-table">
          <thead>
            <tr>
              <th>Hạng mục</th>
              <th>SL</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            ${linesHtml}
          </tbody>
        </table>
        <div class="payment-invoice-total">Số tiền ${isPaid ? "đã" : "cần"} thanh toán: ${formatMoney(invoice.amount)}</div>
      </div>
    `;
    })
    .join("");

  listElement.innerHTML = html;
}

let activePaymentDetailId = null;

async function openPaymentPopupByDetail(button) {
  const detailId = Number(button?.dataset?.detailId || 0);
  const bookingId = Number(button?.dataset?.bookingId || 0);
  const booking = bookingData.find((b) => b.bookingId === bookingId);

  if (!detailId || !booking) {
    alert("Không tìm thấy thông tin chi tiết phòng để thanh toán.");
    return;
  }

  const overlay = document.getElementById("paymentPopupOverlay");
  if (!overlay) {
    return;
  }

  activePaymentDetailId = detailId;
  document.getElementById("paymentBookingCode").textContent =
    `#${booking.bookingId} - Detail #${detailId}`;
  document.getElementById("paymentCustomerInfo").innerHTML = `
    <div><strong>Khách hàng:</strong> ${escapeHtml(booking.customerName || "N/A")}</div>
    <div><strong>Số điện thoại:</strong> ${escapeHtml(booking.customerPhone || "N/A")}</div>
    <div><strong>Email:</strong> ${escapeHtml(booking.customerEmail || "N/A")}</div>
  `;

  try {
    const response = await fetch(`/payments/detail/${detailId}/summary`, {
      method: "GET",
    });
    if (!response.ok) {
      throw new Error("Không thể tải dữ liệu hóa đơn");
    }

    const summary = await response.json();
    document.getElementById("paidTotalAmount").textContent = formatMoney(
      summary.paidTotal,
    );
    document.getElementById("unpaidTotalAmount").textContent = formatMoney(
      summary.unpaidTotal,
    );

    renderInvoiceList(
      document.getElementById("paidInvoiceList"),
      summary.paidInvoices,
      true,
    );
    renderInvoiceList(
      document.getElementById("unpaidInvoiceList"),
      summary.unpaidInvoices,
      false,
    );

    const payButton = document.getElementById("confirmPayBtn");
    payButton.disabled = Number(summary.unpaidTotal || 0) <= 0;
    payButton.textContent = payButton.disabled
      ? "Đã thanh toán đầy đủ"
      : "Thanh toán & tải hóa đơn txt";

    overlay.classList.add("active");
  } catch (error) {
    console.error(error);
    alert("Không tải được thông tin hóa đơn. Vui lòng thử lại.");
  }
}

function closePaymentPopup() {
  const overlay = document.getElementById("paymentPopupOverlay");
  if (!overlay) {
    return;
  }
  overlay.classList.remove("active");
}

async function submitBookingPayment() {
  if (!activePaymentDetailId) {
    return;
  }

  if (!confirm("Xác nhận thanh toán các khoản chưa thanh toán?")) {
    return;
  }

  const token = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content;

  try {
    const response = await fetch(
      `/payments/pay-detail/${activePaymentDetailId}`,
      {
        method: "POST",
        headers: {
          ...(header && token ? { [header]: token } : {}),
        },
      },
    );

    if (!response.ok) {
      throw new Error("Thanh toán thất bại");
    }

    const blob = await response.blob();
    const fileUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = fileUrl;
    link.download = `invoice-detail-${activePaymentDetailId}.txt`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(fileUrl);

    alert("Thanh toán thành công! Hóa đơn txt đã được tải về.");
    closePaymentPopup();
    window.location.reload();
  } catch (error) {
    console.error(error);
    alert("Không thể thanh toán. Vui lòng thử lại.");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const closeButton = document.getElementById("paymentPopupClose");
  const overlay = document.getElementById("paymentPopupOverlay");
  const payButton = document.getElementById("confirmPayBtn");

  closeButton?.addEventListener("click", closePaymentPopup);
  payButton?.addEventListener("click", submitBookingPayment);

  overlay?.addEventListener("click", (event) => {
    if (event.target === overlay) {
      closePaymentPopup();
    }
  });
});
