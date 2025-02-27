package com.apec.poo.utils;

import com.apec.poo.entities.Transaction;
import com.opencsv.CSVWriter;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class CsvService {

//    public void generateCsv(String filePath, Transaction transaction) {
//        String[] header = { "Client name", "Product name", "Product code", "Quantity", "Registration date",
//                "Price", "Total quantity", "Total price", "Transaction date"};
//
//        String[] content = {transaction.getClient().getName(), transaction.getProduct().getName(),
//                transaction.getProduct().getCode(), String.valueOf(transaction.getProduct().getQuantity()),
//                transaction.getProduct().getRegistryDate().toString(), transaction.getProduct().getPrice().toString(),
//                transaction.getQuantityTransaction().toString(), transaction.getTotalPrice().toString(),
//                transaction.getTransactionDate().toString()};
//
//        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
//            writer.writeNext(header);
//            writer.writeAll(Collections.singleton(content));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void generateArrayCsv(String filePath, List<Transaction> transaction) {
        String[] header = { "Client name", "Product name", "Product code", "Quantity available", "Registration date",
                "Price", "Total quantity bought", "Total price", "Transaction date"};

        List<String[]> content = transaction.stream().map(t -> new String[]{
            t.getClient().getName() + " " + t.getClient().getLastName(),
            t.getProduct().getName(),
            t.getProduct().getCode(),
            String.valueOf(t.getProduct().getQuantity()),
            t.getProduct().getRegistryDate().toString(),
            t.getProduct().getPrice().toString(),
            t.getQuantityTransaction().toString(),
            t.getTotalPrice().toString(),
            t.getTransactionDate().toString()
        }).toList();

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(header);
            writer.writeAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}