package com.ey.orderapi.services;

import com.ey.orderapi.exceptions.StockQtyNotAvailable;
import com.ey.orderapi.models.Stock;
import com.ey.orderapi.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public Stock addStock(Stock stock){

        if(stock.getAvailableQty()>0)
            return stockRepository.save(stock);
        else
            throw new StockQtyNotAvailable("Stock Qty should be above 0");
    }

    public Stock isStockAvailable(long productId){
        return stockRepository.findById(productId).orElseThrow(()->new StockQtyNotAvailable("Stock not available"));
    }
}
