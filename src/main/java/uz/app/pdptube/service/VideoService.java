package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.pdptube.dto.VideoDTO;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.ChannelOwnerRepository;
import uz.app.pdptube.repository.UserRepository;
import uz.app.pdptube.repository.VideoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final ChannelService channelService;



    public ResponseMessage getVideo(Integer id){
        Optional<Video> optionalVideo = videoRepository.findById(id);

        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            video.setViews(video.getViews()+1);
            videoRepository.save(video);
            return new ResponseMessage(true, "here is the video", video);
        }else {
            return new ResponseMessage(false, "video with this id doesn't exist", id);
        }
    }
    public ResponseMessage getVideos(){
        List<Video> videos = videoRepository.findAll();
        System.out.println(videos);
        Integer age = Helper.getCurrentPrincipal().getAge();
        List<Video> filteredVideos = videos.stream().filter(video -> video.getAgeRestriction() <= age).toList();
        System.out.println("--------");
        System.out.println(filteredVideos);
        return new ResponseMessage(true, "Here are all the videos", filteredVideos);
    }
    public ResponseMessage postVideo(VideoDTO videoDTO) {
        ResponseMessage responseMessage = channelService.getChannel();
        boolean success = responseMessage.success();
        if (success) {
            Channel channel = (Channel) responseMessage.data();
            Video video = Video.builder()
                    .videoLink(videoDTO.getVideoLink())
                    .channel(channel)
                    .description(videoDTO.getDescription())
                    .likes(0)
                    .dislikes(0)
                    .title(videoDTO.getTitle())
                    .ageRestriction(videoDTO.getAgeRestriction())
                    .category(videoDTO.getCategory())
                    .views(0)
                    .build();
            videoRepository.save(video);
            return new ResponseMessage(true, "Video posted", video);
        }else {
            return new ResponseMessage(false, "you don't have a channel , so you can't post video", videoDTO);
        }
    }
    public ResponseMessage likeVideo(Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            video.setLikes(video.getLikes() + 1);
            videoRepository.save(video);
            return new ResponseMessage(true, "Video liked", video);
        }else{
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }
    public ResponseMessage dislikeVideo(Integer videoId){
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            video.setDislikes(video.getDislikes()+1);
            videoRepository.save(video);
            return new ResponseMessage(true, "Video disliked", video);
        }else {
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }

}
