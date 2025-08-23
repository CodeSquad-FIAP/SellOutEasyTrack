package com.sellout.service;

import com.sellout.model.ImportResult;
import java.io.File;

public interface ImportService {
    ImportResult importFromCsv(File csvFile);
    ImportResult importFromText(File textFile);
    ImportResult importFromWhatsApp(String whatsappText);
}