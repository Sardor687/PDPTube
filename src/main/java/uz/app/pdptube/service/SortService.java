package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.enums.Category;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.VideoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SortService {
    private final VideoRepository videoRepository;
    private final ChannelService channelService;

    public ResponseMessage getYourVideosByUser() {
        ResponseMessage channelServiceResponse = channelService.getChannel();
        if (channelServiceResponse.success()) {
            Channel channel = (Channel) channelServiceResponse.data();
            List<Video> videoList = videoRepository.findByChannel(channel);
            List<Video> filteredVideos = videoList.stream().filter(video -> Helper.fileUploaded(video.getVideoLink())).toList();
            return new ResponseMessage(true, "here are your videos", filteredVideos);
        }else {
            return channelServiceResponse;
        }
    }

    public ResponseMessage getVideosByCategory(String category) {
        try {
            Category categoryEnum = Category.fromString(category);

            List<Video> videos = videoRepository.findByCategory(categoryEnum);

            if (videos.isEmpty()) {
                return new ResponseMessage(false, "No videos found for category: " + category, null);
            }
            List<Video> filteredVideos = videos.stream().filter(video -> Helper.fileUploaded(video.getVideoLink())).toList();

            return new ResponseMessage(true, "Videos retrieved successfully", filteredVideos);

        } catch (IllegalArgumentException e) {
            return new ResponseMessage(false, "Invalid category: " + category, null);
        }
    }
}
