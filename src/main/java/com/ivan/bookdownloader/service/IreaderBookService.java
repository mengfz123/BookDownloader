package com.ivan.bookdownloader.service;

import java.io.IOException;
import java.util.List;

public interface IreaderBookService {

    List<String> getResource(String url, String cookie) throws IOException;
}
