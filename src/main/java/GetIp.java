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

class GetIp extends Thread{
    public static ConcurrentLinkedQueue<ProxyIp> proxyIps = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ProxyIp> proxyIps2 = new ConcurrentLinkedQueue<>();
    public static void setProxyIps(ConcurrentLinkedQueue<ProxyIp> proxyIps) {
        GetIp.proxyIps = proxyIps;
    }
    String URL;

    public void setURL(String URL) {
        this.URL = URL;
    }
    GetIp(ConcurrentLinkedQueue<ProxyIp> proxyIps,String URL){
        GetIp.proxyIps =proxyIps;
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
                request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36 Edg/103.0.1264.62");
                request.addHeader("Cookie","channelid=0; sid=1657880766680508");
                try {
                    //3.执行get请求，相当于在输入地址栏后敲回车键
                    response = httpClient.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity httpEntity = response.getEntity();
                        String html = EntityUtils.toString(httpEntity, "utf-8");
                        Document document = Jsoup.parse(html);
                        Elements elements = document.select("body > [class=body] > [id=content] > [class=con-body] > div > [id=list] > [class=table table-bordered table-striped] > tbody > tr");

                        for (Element element : elements) {

                            Elements elements1 = element.select("[data-title=IP]");
                            Element element2 = elements1.first();
                            String ip = element2.ownText();
                            elements1 = element.select("[data-title=PORT]");
                            element2 = elements1.first();
                            String port = element2.ownText();
                            ProxyIp proxyIp = new ProxyIp();
                            proxyIp.setIp(ip);
                            int port_ = Integer.parseInt(port);
                            proxyIp.setPort(port_);
                            if (proxyIp.test(ip, port_)) {
                                int id = proxyIps2.size() + 1;
                                proxyIp.setId(id);
                                proxyIps2.add(proxyIp);
                            }


                        }
                        while(!proxyIps2.isEmpty()){
                            proxyIps.add(proxyIps2.poll());
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
