package com.cryptocoinnews.scraper;

import com.cryptocoinnews.cryptocoins.data.CryptoCoinsMapper;
import com.cryptocoinnews.cryptocoins.data.entity.CryptoCoins;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoCoinsScraper {

   private final String url= "https://coinmarketcap.com/";
   private final Connection connection = Jsoup.connect("https://coinmarketcap.com/");
   private final CryptoCoinsMapper cryptoCoinsMapper;

    public void getAndInsertTopTenCoins(LocalDateTime timeOfExecution) throws IOException {
        Document pageToScrape = connection.get();
        Elements elements = pageToScrape.getElementsByTag("tr");
        CryptoCoins coin = new CryptoCoins();
        coin.setTimeOfExecution(timeOfExecution);
        for (int i = 1; i < 11; i++) {
            String position = elements.get(i).childNode(1).childNode(0).childNode(0).toString();
            String name = elements.get(i).childNode(2).childNode(0).childNode(0).childNode(0).childNode(1).childNode(0).childNode(0).toString();
            String price = elements.get(i).childNode(3).childNode(0).childNode(0).childNode(0).childNode(0).toString();
            String hourlyPercentage = elements.get(i).childNode(4).childNode(0).childNode(1).toString();
            String dailyPercentage = elements.get(i).childNode(5).childNode(0).childNode(1).toString();
            String weeklyPercentage = elements.get(i).childNode(6).childNode(0).childNode(1).toString();
            String marketCap = elements.get(i).childNode(7).childNode(0).childNode(1).childNode(0).toString();
            String dailyVolume = elements.get(i).childNode(8).childNode(0).childNode(0).childNode(0).childNode(0).toString();
            String circulatingSupply = elements.get(i).childNode(9).childNode(0).childNode(0).childNode(0).childNode(0).toString();

            coin.setPosition(position);
            coin.setName(name);
            coin.setPrice(price);
            coin.setHourlyPercentage(hourlyPercentage);
            coin.setDailyPercentage(dailyPercentage);
            coin.setWeeklyPercentage(weeklyPercentage);
            coin.setMarketCap(marketCap);
            coin.setDailyVolume(dailyVolume);
            coin.setCirculatingSupply(circulatingSupply);

            insertCoinToDatabase(coin);
        }
    }

    private boolean insertCoinToDatabase(CryptoCoins coin) {
        try {
                this.cryptoCoinsMapper.insertCryptoCoin(coin);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}
