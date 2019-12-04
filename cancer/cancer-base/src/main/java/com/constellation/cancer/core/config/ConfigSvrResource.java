package com.constellation.cancer.core.config;

import org.springframework.core.io.AbstractResource;

import java.io.*;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:02 AM
 */
public class ConfigSvrResource extends AbstractResource {

    private ByteArrayOutputStream baos = null;
    private String location;


    public ConfigSvrResource(String location) throws IOException {
        boolean needToStore = location.endsWith("#");
        if (needToStore) {
            this.location = location.substring(0, location.length() - 1);
        } else {
            this.location = location;
        }

        InputStream is = this.getInputStream();
        if (is != null) {
            if (needToStore) {
                this.storeToFile(is);
            }

            is.close();
        }

    }


    public String getDescription() {
        StringBuilder sb = new StringBuilder("configsvr resource[");
        sb.append(this.location).append("]");
        return sb.toString();
    }

    public InputStream getInputStream() throws IOException {
        if (this.baos == null) {
            InputStream inputStream = this.retrieveFromConfigsvr();
            if (inputStream == null) {
                throw new IOException("Could not find resource " + this.location + " from config server");
            }

            this.refresh(inputStream);
            inputStream.close();
        }

        return new ByteArrayInputStream(this.baos.toByteArray());
    }

    private void refresh(InputStream inputStream) throws IOException {
        this.baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int len;
        while((len = inputStream.read(buffer)) > -1) {
            this.baos.write(buffer, 0, len);
        }

        this.baos.flush();
    }

    private InputStream retrieveFromConfigsvr() {
        return ConfigServerClientFactory.getConfigServerClient().getConfigContent(this.location);
    }

    private void storeToFile(InputStream is) throws IOException {
        File f = new File(this.getClass().getClassLoader().getResource("").getPath() + this.location);
        File p = f.getParentFile();
        if (!p.exists()) {
            p.mkdirs();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        FileOutputStream os = new FileOutputStream(f);
        boolean var5 = true;

        int data;
        while((data = is.read()) != -1) {
            os.write(data);
        }

        os.flush();
        os.close();
    }
}
