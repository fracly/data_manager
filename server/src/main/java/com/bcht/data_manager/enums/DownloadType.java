package com.bcht.data_manager.enums;

/**
 * user type
 */
public enum DownloadType {
    CSV(".csv", 1), TXT(".txt", 2);

    private String name;
    private int index;

    private DownloadType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static DownloadType valueOf(int idx) {
        for(DownloadType downloadType : DownloadType.values()) {
            if(downloadType.getIndex() == idx) {
                return downloadType;
            }
        }
        return null;
    }
}
