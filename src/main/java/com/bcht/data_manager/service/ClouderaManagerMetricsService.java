package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.ClouderaEntity;
import com.bcht.data_manager.utils.PropertyUtils;
import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.model.ApiTimeSeries;
import com.cloudera.api.model.ApiTimeSeriesData;
import com.cloudera.api.model.ApiTimeSeriesResponse;
import com.cloudera.api.model.ApiTimeSeriesResponseList;
import com.cloudera.api.v11.TimeSeriesResourceV11;
import com.cloudera.api.v32.RootResourceV32;

import java.text.SimpleDateFormat;
import java.util.*;

public class ClouderaManagerMetricsService {

    static RootResourceV32 apiRoot;

    static {
        apiRoot = new ClouderaManagerClientBuilder().withHost(PropertyUtils.getString("cluster.ip"))
                .withUsernamePassword(PropertyUtils.getString("cluster.user.name"), PropertyUtils.getString("cluster.user.password"))
                .build()
                .getRootV32();
    }

    public static List<ClouderaEntity> getClusterLoadInfo(int type) {
        List<ClouderaEntity> clouderaEntities = new ArrayList<>();
        TimeSeriesResourceV11 timeSeriesResourceV11 = apiRoot.getTimeSeriesResource();
        long currentTime = System.currentTimeMillis();
        currentTime -= 30 * 60 * 1000;
        Date halfHourAgo = new Date(currentTime);
        SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date now = new Date();
        ApiTimeSeriesResponseList response = null;
        switch(type) {
            case 1:
                response = timeSeriesResourceV11.queryTimeSeries("SELECT total_bytes_written_rate_across_datanodes, total_bytes_read_rate_across_datanodes WHERE entityName = 'hdfs' AND category = SERVICE", tmp.format(halfHourAgo) + ":00", tmp.format(now) + ":00");
                break;
            case 2:
                response = timeSeriesResourceV11.queryTimeSeries("select total_bytes_receive_rate_across_network_interfaces, total_bytes_transmit_rate_across_network_interfaces where category = CLUSTER", tmp.format(halfHourAgo) + ":00", tmp.format(now) + ":00");
                break;
            case 3:
                response = timeSeriesResourceV11.queryTimeSeries("SELECT total_write_bytes_rate_across_disks, total_read_bytes_rate_across_disks WHERE entityName = '1' AND category = CLUSTER", tmp.format(halfHourAgo) + ":00", tmp.format(now) + ":00");
                break;
            case 4:
                response = timeSeriesResourceV11.queryTimeSeries("SELECT cpu_percent_across_hosts, cpu_percent_across_hosts WHERE entityName = '1' AND category = CLUSTER", tmp.format(halfHourAgo) + ":00", tmp.format(now) + ":00");
                break;
        }

        for(ApiTimeSeriesResponse xx : response.getResponses()) {
            List<ApiTimeSeries> list = xx.getTimeSeries();
            List<ApiTimeSeriesData> writtenTimeSeries = list.get(0).getData();
            List<ApiTimeSeriesData> readTimeSeries = list.get(1).getData();
            for(int i = 0; i < writtenTimeSeries.size(); i ++) {
                ClouderaEntity clouderaEntity = new ClouderaEntity();
                clouderaEntity.setType(writtenTimeSeries.get(i).getType());
                clouderaEntity.setxAxis(writtenTimeSeries.get(i).getTimestamp());
                clouderaEntity.setyAxis1(writtenTimeSeries.get(i).getValue());
                clouderaEntity.setyAxis2(readTimeSeries.get(i).getValue());
                clouderaEntities.add(clouderaEntity);
            }
        }
        return clouderaEntities;
    }

    public static Map<String, Object> getClusterRealTimeInfo() {
        Map<String, Object> resultMap = new HashMap<>();
        TimeSeriesResourceV11 timeSeriesResourceV11 = apiRoot.getTimeSeriesResource();
        ApiTimeSeriesResponseList cpuResponse = timeSeriesResourceV11.queryTimeSeries("SELECT cpu_percent_across_hosts WHERE category=CLUSTER", null, null);
        ApiTimeSeriesResponse apiTimeSeriesResponse = cpuResponse.get(0);
        List<ApiTimeSeries> list = apiTimeSeriesResponse.getTimeSeries();
        ApiTimeSeries apiTimeSeries = list.get(0);
        List<ApiTimeSeriesData>  tmp = apiTimeSeries.getData();
        resultMap.put("cpu", tmp.get(tmp.size() - 1).getValue());

        ApiTimeSeriesResponseList memoryResponse = timeSeriesResourceV11.queryTimeSeries("SELECT 100 * total_physical_memory_used_across_hosts/total_physical_memory_total_across_hosts WHERE category=CLUSTER", null, null);
        ApiTimeSeriesResponse apiTimeSeriesResponse2 = memoryResponse.get(0);
        List<ApiTimeSeries> list2 = apiTimeSeriesResponse2.getTimeSeries();
        ApiTimeSeries apiTimeSeries2 = list2.get(0);
        tmp = apiTimeSeries2.getData();
        resultMap.put("memory", tmp.get(tmp.size() - 1).getValue());

        ApiTimeSeriesResponseList diskIOResponse = timeSeriesResourceV11.queryTimeSeries("SELECT total_read_bytes_rate_across_disks WHERE  category = CLUSTER", null, null);
        ApiTimeSeriesResponse apiTimeSeriesResponse3 = diskIOResponse.get(0);
        List<ApiTimeSeries> list3 = apiTimeSeriesResponse3.getTimeSeries();
        ApiTimeSeries apiTimeSeries3 = list3.get(0);
        tmp = apiTimeSeries3.getData();
        resultMap.put("disk", tmp.get(tmp.size() - 1).getValue());

        return resultMap;
    }
}

