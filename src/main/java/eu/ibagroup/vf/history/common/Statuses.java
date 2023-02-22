package eu.ibagroup.vf.history.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Statuses {
    FAILED("Failed"), SUCCEEDED("Succeeded");
    private final String status;

    /**
     * Getting enum status value.
     *
     * @return enum status value
     */
    public String toString() {
        return this.status;
    }
}