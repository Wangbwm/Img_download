import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GetIP_QIYUN extends Thread{
   ConcurrentLinkedQueue<ProxyIp> proxyIps = new ConcurrentLinkedQueue<>();

    public static void setProxyIps(ConcurrentLinkedQueue<ProxyIp> proxyIps) {
        GetIp.proxyIps = proxyIps;
    }

    public String URL= "";

    public void setURL(String URL) {
        this.URL = URL;
    }
    GetIP_QIYUN(ConcurrentLinkedQueue<ProxyIp> proxyIps,String URL){
        this.proxyIps=proxyIps;
        this.URL=URL;
    }
    public void run() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet(URL);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1000)//设置创建连接的最长时间
                .setConnectionRequestTimeout(500)//设置获取连接的最长时间
                .setSocketTimeout(10 * 1000)//设置数据传输的最长时间
                .setCookieSpec(CookieSpecs.STANDARD)
                //.setProxy(proxy)
                .build();

        request.setConfig(requestConfig);
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        request.addHeader("Cookie","http_waf_cookie=2c9b27c3-4c09-48836f05b6c574304478fe303bcff3d19858");
        try {
            //3.执行get请求，相当于在输入地址栏后敲回车键
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                String html = EntityUtils.toString(httpEntity, "gb2312");
                Document document = Jsoup.parse(html);
                Elements elements = document.select("#list > table > tbody > tr");

                for (Element element : elements) {

                    Elements elements1 = element.select("td");
                    String ip = elements1.first().ownText();
                    String port = elements1.get(1).ownText();
                    String Degree = elements1.get(2).ownText();
                    if(Degree.compareTo("高匿代理IP")==0&&ip.compareTo("")!=0&&port.compareTo("")!=0) {
                        ProxyIp proxyIp = new ProxyIp();
                        proxyIp.setIp(ip);
                        int port_ = Integer.parseInt(port);
                        proxyIp.setPort(port_);
                        if (proxyIp.test(ip, port_)) {
                            int id = proxyIps.size() + 1;
                            proxyIp.setId(id);
                            proxyIps.add(proxyIp);
                        }


                    }
                }


            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("返回状态不是200");
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);

        }
    }
}
