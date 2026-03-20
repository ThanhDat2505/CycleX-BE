package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.Dispute;

public class DisputeResultResponse {

    public Integer disputeId;
    public String status;
    public String result;
    public String message;
    public String resolvedAt;

    public static DisputeResultResponse from(Dispute d) {
        DisputeResultResponse res = new DisputeResultResponse();
        res.disputeId = d.getDisputeId();
        res.status = d.getStatus().name();

        // Map resolution action to result
        if (d.getResolutionAction() != null) {
            switch (d.getResolutionAction()) {
                case "REFUND_BUYER":
                case "BUYER_WIN":
                    res.result = "BUYER_WIN";
                    res.message = "Khiếu nại đã được chấp nhận. Người mua sẽ được hoàn tiền.";
                    break;
                case "RELEASE_FUND_SELLER":
                case "SELLER_WIN":
                    res.result = "SELLER_WIN";
                    res.message = "Khiếu nại đã bị từ chối. Người bán được giữ tiền.";
                    break;
                case "SPLIT":
                    res.result = "SPLIT";
                    res.message = "Quản trị viên đã quyết định xử lý chia đều.";
                    break;
                case "CLOSE_CASE":
                    res.result = "CLOSED";
                    res.message = "Khiếu nại đã được đóng sau khi xác minh.";
                    break;
                default:
                    res.result = d.getStatus().name();
                    res.message = "Khiếu nại đang được xử lý.";
                    break;
            }
        } else {
            res.result = d.getStatus().name();
            switch (d.getStatus().name()) {
                case "OPEN":
                    res.message = "Khiếu nại đang chờ được xử lý.";
                    break;
                case "IN_PROGRESS":
                    res.message = "Khiếu nại đang được kiểm duyệt viên xem xét.";
                    break;
                case "NEED_MORE_INFO":
                    res.message = "Vui lòng cung cấp thêm thông tin để tiếp tục xử lý.";
                    break;
                case "ESCALATED":
                    res.message = "Khiếu nại đã được chuyển lên quản trị viên.";
                    break;
                default:
                    res.message = "Khiếu nại đang được xử lý.";
                    break;
            }
        }

        res.resolvedAt = d.getResolvedAt() != null ? d.getResolvedAt().toString() : null;
        return res;
    }
}
