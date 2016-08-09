package uk.gov.digital.ho.proving.income.domain.api;

/**
 * @Author Home Office Digital
 */
public enum ErrorCode {

    MISSING_PARAMETER("0001", "Missing parameter: "),
    INVALID_PARAMETER_TYPE("0002", "Invalid parameter: "),
    INVALID_PARAMETER_FORMAT("0003", "Invalid parameter: "),
    INVALID_PARAMETER_VALUE("0004", "Invalid parameter: "),
    INTERNAL_ERROR("0005", "Internal server error"),
    API_SERVER_ERROR("0006", "Error at Income Proving API server"),
    API_CLIENT_ERROR("0007", "Bad request to Income Proving API server"),
    API_CONNECTION_ERROR("0008", "Error connecting to the Income Proving API server");

    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
