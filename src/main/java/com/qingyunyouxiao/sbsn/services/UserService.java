package com.qingyunyouxiao.sbsn.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.qingyunyouxiao.sbsn.dto.*;

@Service
public class UserService {
    
    public ProfileDto getProfile(int userId) {
        return new ProfileDto(new UserSummaryDto(1L, "Philip", "Harder"), 
                Arrays.asList(new UserSummaryDto(2L,"Alexander", "Weasley")),
                Arrays.asList(new MessageDto(1L, "Message")),
                Arrays.asList(new ImageDto(1L, "Title", null)));
    }

    public void addFriend(Long friendId) {
        return;
    }
    
    public List<UserSummaryDto> searchUsers(String term) {
        return Arrays.asList(new UserSummaryDto(1L, "Philip", "Harder"),
                new UserSummaryDto(2L,"Alexander", "Weasley"));
    }
}
