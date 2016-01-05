package nl.teunwillems.server;

import java.io.Serializable;

/**
 * Created by Teun on 5-1-2016.
 */
public class Request implements Serializable {

    public enum REQUEST_TYPE {
        WHOLE, DYNAMIC
    }

    private REQUEST_TYPE request_type;
    private int level;

    public Request(REQUEST_TYPE request_type, int level) {
        this.request_type = request_type;
        this.level = level;
    }

    public REQUEST_TYPE getRequestType() {
        return request_type;
    }

    public int getLevel() {
        return level;
    }
}
