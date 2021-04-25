package com.sensorweb.datacenterlaadsservice.util;

import org.springframework.stereotype.Component;

@Component
public interface LAADSConstant {
    String LAADS_DOWNLOAD_TOKEN = "Bearer c2Vuc29yd2ViOk1URXdOek0zTWpneE5rQnhjUzVqYjIwPToxNjE3MDIzODc4OmQ2NGRkYzBlMDc4Mzc2MDgyNDRiMGI3NzM5NjFjYjc5MjkyYzBiZWQ";

//    String LAADS_DOWNLOAD_TOKEN = "Bearer 40332b1d5cd1e21a85399b2f07ce59b7335430cdca7deee9d895b095a8bda740";

    String LAADS_Web_Service = "https://modwebsrv.modaps.eosdis.nasa.gov/axis2/services/MODAPSservices";
}
