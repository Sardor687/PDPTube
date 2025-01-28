package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.dto.CommentDTO;
import uz.app.pdptube.dto.ReplyDTO;
import uz.app.pdptube.entity.*;
import uz.app.pdptube.entity.UserDislikedComments;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final UserLikedCommentsRepository likedCommentsRepository;
    private final UserDislikedCommentsRepository dislikedCommentsRepository;
    private final UserLikedCommentsRepository userLikedCommentsRepository;
    private final UserDislikedCommentsRepository userDislikedCommentsRepository;

    public ResponseMessage getVideoComments(Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (!optionalVideo.isPresent()) {
            return new ResponseMessage(false, "video doesn't exist with this videoId: " + videoId, null);
        }
        Video video = optionalVideo.get();
        if (Helper.ageRestricted(video)) {
            return new ResponseMessage(false, "you are not old enough!!!", null);
        }
        Optional<List<Comment>> optionalComments = commentRepository.findAllByVideoId(videoId);
        if (optionalComments.isPresent()) {
            List<Comment> comments = optionalComments.get();
            int size = comments.size();
            if (size > 0) {
                return new ResponseMessage(true, "here are the comments to the videoId "+ videoId, comments );
            }else {
                return new ResponseMessage(true, "there are no comments to the videoId "+ videoId, comments );
            }
        }else {
            return new ResponseMessage(false, "no comments found for this video id", videoId);
        }
    }





    public ResponseMessage addReply(ReplyDTO replyDTO) {
        Integer parentId = replyDTO.getParentComment();
        boolean existsById = commentRepository.existsById(parentId);
        if (existsById){
            Comment comment = Comment.builder()
                    .author(Helper.getCurrentPrincipal())
                    .parentCommentId(replyDTO.getParentComment())
                    .likes(0)
                    .dislikes(0)
                    .text(replyDTO.getText())
                    .build();
            commentRepository.save(comment);
            return new ResponseMessage(true, "replied to comment with id " + replyDTO.getParentComment() , comment);
        }else {
            return new ResponseMessage(false,"comment with this id doesn't exist , so you cant reply!", replyDTO);
        }
    }




    // Izohga like qo'shish
    @Transactional
    public ResponseMessage addLike(Integer commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return new ResponseMessage(false, "Comment not found", commentId);
        }else {
            Comment comment = commentOpt.get();
            if (userLikedCommentsRepository.findByComment(comment.getId()).isPresent()) {
                return new ResponseMessage(false, "Comment already liked before", comment);
            }else {
                Optional<UserDislikedComments> byComment = userDislikedCommentsRepository.findByComment(commentId);
                if (byComment.isPresent()) {
                    comment.setDislikes(comment.getDislikes() - 1);
                    comment.setLikes(comment.getLikes() + 1);
                    commentRepository.save(comment);
                    userDislikedCommentsRepository.delete(byComment.get());
                    UserLikedComments userLikedComments = new UserLikedComments();
                    userLikedComments.setOwner(Helper.getCurrentPrincipal().getId());
                    userLikedComments.setComment(comment.getId());
                    userLikedCommentsRepository.save(userLikedComments);
                    return new ResponseMessage(true, "Comment liked successfully", comment);
                }else {
                    comment.setLikes(comment.getLikes() + 1);
                    commentRepository.save(comment);
                    UserLikedComments userLikedComments = new UserLikedComments();
                    userLikedComments.setComment(comment.getId());
                    userLikedComments.setOwner(Helper.getCurrentPrincipal().getId());
                    userLikedCommentsRepository.save(userLikedComments);
                    return new ResponseMessage(true, "Successfully liked the comment", comment);
                }
            }
        }
    }

    // Izohga dislike qo'shish
    @Transactional
    public ResponseMessage addDislike(Integer commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if(commentOpt.isPresent()){
            Comment comment = commentOpt.get();
            Optional<UserDislikedComments> optionalRelation = dislikedCommentsRepository.findByComment(comment.getId());
            if (optionalRelation.isPresent()){
                return new ResponseMessage(false, "You already disliked this comment", comment);
            }else {
                Optional<UserLikedComments> userLikedCommentsOptional = userLikedCommentsRepository.findByComment(comment.getId());
                if (userLikedCommentsOptional.isPresent()){
                    comment.setLikes(comment.getLikes() - 1);
                    comment.setDislikes(comment.getDislikes() + 1);
                    commentRepository.save(comment);
                    userLikedCommentsRepository.delete(userLikedCommentsOptional.get());
                    UserDislikedComments dislikedComments = new UserDislikedComments();
                    dislikedComments.setComment(comment.getId());
                    dislikedComments.setOwner(Helper.getCurrentPrincipal().getId());
                    userDislikedCommentsRepository.save(dislikedComments);
                    return new ResponseMessage(true, "Disliked the comment", comment);
                }else{
                    comment.setDislikes(comment.getDislikes() + 1);
                    commentRepository.save(comment);
                    UserDislikedComments dislikedComments = new UserDislikedComments();
                    dislikedComments.setComment(comment.getId());
                    dislikedComments.setOwner(Helper.getCurrentPrincipal().getId());
                    userDislikedCommentsRepository.save(dislikedComments);
                    return new ResponseMessage(true, "Disliked the comment", comment);
                }
            }
        }else {
            return new ResponseMessage(false, "Comment not found", commentId);
        }
    }


    // Foydalanuvchi video egasi ekanligini tekshirish
    private Boolean isVideoOwner(Integer videoId, User user) {
        Video video = videoRepository.findById(videoId).orElse(null);
        Channel channel = video.getChannel();
        Integer owner;
        Optional<ChannelOwner> optionalRelation = channelOwnerRepository.findByChannel(channel.getId());
        if (optionalRelation.isPresent()){
            ChannelOwner relation = optionalRelation.get();
             owner = relation.getOwner();
        }else{
            return null;
        }
        return video != null && owner.equals(user.getId());
    }


    // Video egasining izohlarni o'chirishni tekshiruvchi metod
    @Transactional
    public ResponseMessage deleteAllCommentsByVideo(Integer videoId) {
        User currentUser = Helper.getCurrentPrincipal();

        // Video tekshiruvi
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isEmpty()) {
            return new ResponseMessage(false, "Video not found", videoId);
        }
        Boolean videoOwner = isVideoOwner(videoId, currentUser);
        if (videoOwner == null) {
            return new ResponseMessage(false, "video bor , lekin channel yoki bolmasam channelOwner relation yo'q , backendda xato", null);
        }
        boolean b = videoOwner.booleanValue();
        if (!b) {
            return new ResponseMessage(false, "You are not the owner of this video", videoId);
        }

        commentRepository.deleteAllByVideoId(videoId);
        return new ResponseMessage(true, "All comments deleted for videoId " + videoId, null);
    }
    // Izohni o'chirish (Foydalanuvchi o'z izohini o'chirishi mumkin)


    @Transactional
    public ResponseMessage deleteComment(Integer commentId) {
        User currentUser = Helper.getCurrentPrincipal();

        // Izohni olish va tekshirish
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return new ResponseMessage(false, "Comment not found", commentId);
        }

        Comment comment = commentOpt.get();

        // Foydalanuvchi o'z izohini o'chirishi mumkin
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            return new ResponseMessage(false, "You do not have permission to delete this comment", commentId);
        }

        commentRepository.deleteByParentCommentId(comment.getId());
        commentRepository.delete(comment);
        return new ResponseMessage(true, "Comment deleted successfully", commentId);
    }


    // Izoh qo'shish
    @Transactional
    public ResponseMessage postComment(CommentDTO commentDTO) {
        Optional<Video> optionalVideo = videoRepository.findById(commentDTO.getVideoId());
        if (optionalVideo.isPresent()){
            Comment comment = Comment.builder()
                    .author(Helper.getCurrentPrincipal())
                    .likes(0)
                    .dislikes(0)
                    .text(commentDTO.getText())
                    .videoId(commentDTO.getVideoId())
                    .build();
            commentRepository.save(comment);
            return new ResponseMessage(true, "Comment posted successfully", comment);
        }else{
            return new ResponseMessage(false, "video with this id doesn't exist  " + commentDTO.getVideoId(), commentDTO );
        }

       /* if (parentCommentId != null && videoId != null)
        // Video tekshiruvi
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            return new ResponseMessage(false, "Video not found", videoId);
        }

        // Hozirgi foydalanuvchi
        User user = Helper.getCurrentPrincipal();

        // Izohni yaratish
        Comment comment = Comment.builder()
                .author(user)  // Foydalanuvchi
                .videoId(videoId)  // Video id
                .text(text)  // Izoh matni
                .parentCommentId(parentCommentId != null ? parentCommentId : 0)  // Agar izohga javob bo'lsa
                .build();
        commentRepository.save(comment);

        return new ResponseMessage(true, "Comment posted successfully", comment);*/
    }
}
