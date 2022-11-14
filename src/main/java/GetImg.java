import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GetImg{
    ConcurrentLinkedQueue<ProxyIp> proxyIps = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> ImgName = new ConcurrentLinkedQueue<>();

    public static void setProxyIps(ConcurrentLinkedQueue<ProxyIp> proxyIps) {
        proxyIps = proxyIps;
    }

    public void setImgName(ConcurrentLinkedQueue<String> imgName) {
        ImgName = imgName;
    }

    boolean is_end=false;

    public boolean end(){
        return this.is_end;
    }
    GetImg(ConcurrentLinkedQueue<ProxyIp> proxyIps,ConcurrentLinkedQueue<String> ImgName){
        this.proxyIps=proxyIps;
        this.ImgName=ImgName;
    }


    public void run() {
        synchronized (proxyIps) {
            for (int i = 1; i < 5; i++) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                //2.创建get请求，相当于在浏览器地址栏输入 网址
                HttpGet request = new HttpGet("https://wallhaven.cc/search?q=Nahida&page="+ i);

                //HttpHost proxy = new HttpHost("61.132.228.99", 8088);
                ProxyIp MyProxy = new ProxyIp();
                boolean IfProxy = false;
                RequestConfig requestConfig = RequestConfig.custom()
                        .build();
                if (!proxyIps.isEmpty()) {
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
                    if (IfProxy) {
                        HttpHost proxy = new HttpHost(MyProxy.getIp(), MyProxy.getPort());
                        requestConfig = RequestConfig.custom()
                                .setConnectTimeout(1000)//设置创建连接的最长时间
                                .setConnectionRequestTimeout(500)//设置获取连接的最长时间
                                .setSocketTimeout(10 * 1000)//设置数据传输的最长时间
                                .setProxy(proxy)
                                .setCookieSpec(CookieSpecs.STANDARD)
                                .build();
                    }
                } else {
                    requestConfig = RequestConfig.custom()
                            .setConnectTimeout(1000)//设置创建连接的最长时间
                            .setConnectionRequestTimeout(500)//设置获取连接的最长时间
                            .setSocketTimeout(10 * 1000)//设置数据传输的最长时间
                            .setCookieSpec(CookieSpecs.STANDARD)
                            //.setProxy(proxy)
                            .build();
                }

                request.setConfig(requestConfig);
                request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                //request.addHeader("Cookie","XSRF-TOKEN=eyJpdiI6IjZaMzY0WG5WY244UTI5MjNvT1YySVE9PSIsInZhbHVlIjoicmJyaVpSNDBGbVdPYk9TM09RdXFyeUw4QXpENXJtMTVyeFM5T0l5R0duUWM2TmRnQ3FhMmdxaW5ybUlTd2tyTiIsIm1hYyI6IjRhNjJjYjFjZWI0YzlkNTRiNmViOGE5ODZlYjViODRkMzgwMTI1NzY0YWI3Njg4ZWUyZjRiN2I2OWMyMzIwOGIifQ==; wallhaven_session=eyJpdiI6Imx0aExuWnhSMmhCV3ZSN0lObFNYSmc9PSIsInZhbHVlIjoiWDdTXC9ZcFNZK1Z3UmZPYkFtMzZJY2xRU1NCNWxkOXBpUGUxNmVzME9Ic0xUTWlOS0xpTTFJVjJ2OE93UTFEZVgiLCJtYWMiOiI4MDVmYWI3NGQ0ZTI5MDg1YjY1YmRiYTlkNzQ3YWJkMjRhZGExZGVmMzVhNTRkNTY4ZmJlODg5ZTg2Nzg2MDg1In0=");
                try {
                    //3.执行get请求，相当于在输入地址栏后敲回车键
                    response = httpClient.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity httpEntity = response.getEntity();
                        String html = EntityUtils.toString(httpEntity, "utf-8");
                        Document document = Jsoup.parse(html);
                        Elements elements = document.select("#thumbs > section > ul > li > figure > a");
                        for (Element element : elements) {

                            String ip = element.attr("href");
                            String[] a = ip.split("/w/");
                            String temp = a[1];
                            String temp2 = temp.substring(0, 2);
                            String temp3 = "https://w.wallhaven.cc/full/";
                            String temp4 = "/wallhaven-";
                            String temp5 = ".jpg";
                            String img = temp3 + temp2 + temp4 + temp + temp5;
                            ImgName.add(img);

                        }
                        ImgName.isEmpty();

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
                    this.is_end=true;
                    //6.关闭
                    HttpClientUtils.closeQuietly(response);
                    HttpClientUtils.closeQuietly(httpClient);
                    proxyIps.notifyAll();
                }
            }

        }
    }


    }


