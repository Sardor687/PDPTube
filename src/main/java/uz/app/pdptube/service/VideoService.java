package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.dto.VideoDTO;
import uz.app.pdptube.entity.*;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.*;

import java.util.List;
import java.util.Optional;

import static uz.app.pdptube.helper.Helper.ageRestricted;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final ChannelService channelService;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final UserLikedVideosRepository userLikedVideosRepository;
    private final UserDislikedVideosRepository userDislikedVideosRepository;
    private final PlaylistVideosRepository playlistVideosRepository;

    public ResponseMessage getVideo(Integer id) {
        Optional<Video> optionalVideo = videoRepository.findById(id);

        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            if (ageRestricted(video)) {
                return new ResponseMessage(false, "you are not old enough!", Helper.getCurrentPrincipal().getAge());
            }
            if (!fileUploaded(video.getVideoLink())){
                return new ResponseMessage(false, "video doesn't have its file yet, first upload the file!", video);
            }
            video.setViews(video.getViews() + 1);
            videoRepository.save(video);
            return new ResponseMessage(true, "here is the video", video);
        } else {
            return new ResponseMessage(false, "video with this id doesn't exist", id);
        }
    }
    public ResponseMessage getVideos() {
        List<Video> videos = videoRepository.findAll();
        Integer age = Helper.getCurrentPrincipal().getAge();
        List<Video> filteredVideos = videos.stream()
                .filter(video -> video.getAgeRestriction() <= age)
                .filter(video -> {
                    return fileUploaded(video.getVideoLink());
                })
                .toList();
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
            return new ResponseMessage(true, "Video posted, now please upload the file!", video);
        } else {
            return new ResponseMessage(false, "you don't have a channel , so you can't post video", videoDTO);
        }
    }
    private boolean fileUploaded(String videoLink){
        return !(videoLink.equalsIgnoreCase("String") || videoLink.isEmpty() || videoLink.isBlank());
    }
    @Transactional
    public ResponseMessage likeVideo(Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            if (ageRestricted(video)) {
                return new ResponseMessage(false, "you are not old enough!", Helper.getCurrentPrincipal().getAge());
            }
            boolean previouslyLiked = userLikedVideosRepository.existsByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId);
            if (previouslyLiked) {
                return new ResponseMessage(false, "you are already liked the video before!", video);
            }else {
                if (!fileUploaded(video.getVideoLink())){
                    return new ResponseMessage(false, "video doesn't have its file yet, first owner must upload the file!", video);
                }
                if (userDislikedVideosRepository.existsByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId)){
                    userDislikedVideosRepository.deleteByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId);
                    video.setLikes(video.getLikes() + 1);
                    video.setDislikes(video.getDislikes() - 1);
                }else {
                    video.setLikes(video.getLikes() + 1);
                }
                videoRepository.save(video);
                UserLikedVideos userLikedVideos = new UserLikedVideos();
                userLikedVideos.setVideo(video.getId());
                userLikedVideos.setOwner(Helper.getCurrentPrincipal().getId());
                userLikedVideosRepository.save(userLikedVideos);
                return new ResponseMessage(true, "Video liked", video);
            }
        } else {
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }


    @Transactional
    public ResponseMessage dislikeVideo(Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            if (ageRestricted(video)) {
                return new ResponseMessage(false, "you are not old enough!", Helper.getCurrentPrincipal().getAge());
            }
            boolean previouslyDisliked = userDislikedVideosRepository.existsByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId);
            if (previouslyDisliked) {
                return new ResponseMessage(false, "you  already disliked the video before!", video);
            }else {
                if (!fileUploaded(video.getVideoLink())){
                    return new ResponseMessage(false, "video doesn't have its file yet, first owner must upload the file!", video);
                }
                if (userLikedVideosRepository.existsByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId)) {
                    userLikedVideosRepository.deleteByOwnerAndVideo(Helper.getCurrentPrincipal().getId(), videoId);
                    video.setDislikes(video.getDislikes() + 1);
                    video.setLikes(video.getLikes() - 1);
                }else {
                    video.setDislikes(video.getDislikes() + 1);
                }
                videoRepository.save(video);
                UserDislikedVideos userDislikedVideos = new UserDislikedVideos();
                userDislikedVideos.setVideo(video.getId());
                userDislikedVideos.setOwner(Helper.getCurrentPrincipal().getId());
                userDislikedVideosRepository.save(userDislikedVideos);
                return new ResponseMessage(true, "Video disliked", video);
            }
        } else {
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }

    @Transactional
    public ResponseMessage deleteVideo(Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            Channel channel = video.getChannel();
            Optional<ChannelOwner> optionalRelation = channelOwnerRepository.findByChannel(channel.getId());
            if (optionalRelation.isPresent()) {
                ChannelOwner relation = optionalRelation.get();
                Integer owner = relation.getOwner();
                if (owner.equals(Helper.getCurrentPrincipal().getId())) {
                    videoRepository.delete(video);
                    userDislikedVideosRepository.deleteByVideo(video.getId());
                    userLikedVideosRepository.deleteByVideo(video.getId());
                    playlistVideosRepository.deleteByVideo(video.getId());
                    return new ResponseMessage(true, "Video deleted", video);
                } else {
                    return new ResponseMessage(false, "you can't delete this video because you are not the owner", videoId);
                }
            } else {
                return new ResponseMessage(false, "channel_owner yo'q , lekin channel bor , backendda xato", channel);
            }
        } else {
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }
}
