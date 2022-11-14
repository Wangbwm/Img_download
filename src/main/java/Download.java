import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Download extends Thread {
    static ConcurrentLinkedQueue<ProxyIp> proxyIps = new ConcurrentLinkedQueue<>();
    static ConcurrentLinkedQueue<String> ImgName = new ConcurrentLinkedQueue<>();
    static int number=1;
    Download(ConcurrentLinkedQueue<String> imgName,ConcurrentLinkedQueue<ProxyIp> proxyIps){
        ImgName=imgName;
        Download.proxyIps =proxyIps;
    }
    public void setImgName(ConcurrentLinkedQueue<String> imgName) {
        ImgName = imgName;
    }

    public void setProxyIps(ConcurrentLinkedQueue<ProxyIp> proxyIps) {
        Download.proxyIps = proxyIps;
    }

    public void setNumber(int number) {
        Download.number = number;
    }

    @Override
    public void run() {
        String o= "";
        String name= "";
        synchronized (ImgName) {
            if (!ImgName.isEmpty()) {
                o = ImgName.poll();
            }
            ImgName.notify();
        }
            try {
                getImg(o, number++);
                sleep(2000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    public void getImg(String imgUrl, int number) throws IOException {
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .build();
            String filepath = "D:/Desktop/test/" + number + ".jpg";

            ProxyIp MyProxy = new ProxyIp();
            boolean IfProxy = false;
            if(!proxyIps.isEmpty()) {
                while (!proxyIps.isEmpty()) {
                    ProxyIp temp = proxyIps.peek();
                    String ip = temp.getIp();
                    Integer port = temp.getPort();
                    if (temp.test(ip, port)) {
                        MyProxy = temp;
                        IfProxy = true;
                        break;
                    } else {
                        proxyIps.poll();
                    }
                }
            }
                if(IfProxy)
                {
                    HttpHost proxy = new HttpHost(MyProxy.getIp(), MyProxy.getPort());
                    requestConfig = RequestConfig.custom()
                            .setConnectTimeout(1000)//设置创建连接的最长时间
                            .setConnectionRequestTimeout(500)//设置获取连接的最长时间
                            .setSocketTimeout(10 * 1000)//设置数据传输的最长时间
                            .setProxy(proxy)
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .build();
                }else{//用本机IP
                    requestConfig = RequestConfig.custom()
                            .setConnectTimeout(1000)//设置创建连接的最长时间
                            .setConnectionRequestTimeout(500)//设置获取连接的最长时间
                            .setSocketTimeout(10 * 1000)//设置数据传输的最长时间
                            //.setProxy(proxy)
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .build();
                }
            HttpClient client = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(imgUrl);
            httpget.setConfig(requestConfig);
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
            HttpResponse response = client.execute(httpget);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                File file = new File(filepath);
                file.getParentFile().mkdirs();
                FileOutputStream FileOut = new FileOutputStream(file);
                /**
                 * 根据实际运行效果 设置缓冲区大小
                 */
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    FileOut.write(buffer, 0, length);
                }
                is.close();
                FileOut.flush();
                FileOut.close();
                System.out.println("图片" + number + "下载完成");
            }
            else{
                System.out.println("图片" + number + "下载错误，可能是网络连接问题");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
