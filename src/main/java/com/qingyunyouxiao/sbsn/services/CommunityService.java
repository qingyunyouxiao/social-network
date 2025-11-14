package com.qingyunyouxiao.sbsn.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.qingyunyouxiao.sbsn.dto.ImageDto;
import com.qingyunyouxiao.sbsn.dto.MessageDto;

@Service
public class CommunityService {
    
    public List<MessageDto> getCommunityMessages(int page) {
        return Arrays.asList(new MessageDto(1L, "First message"),
            new MessageDto(2L, "Second message"));
    }

    public List<ImageDto> getCommunityImages(int page) {
        return Arrays.asList(new ImageDto(1L, "First message", null),
            new MessageDto(2L, "Second message"));
    }

}
