package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.dto.ChannelDTO;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.ChannelRepository;
import uz.app.pdptube.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public User getCurrentPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public ResponseMessage getChannel(){
        User principal = getCurrentPrincipal();
        System.out.println(principal);
        if (principal.getChannel() == null) {
            Optional<Channel> optionalChannel = channelRepository.findByOwnerId(principal.getId());
            if (optionalChannel.isPresent()) {
                Channel channel = optionalChannel.get();
                principal.setChannel(channel);
                return new ResponseMessage(true, "here is the channel", channel);
            }else {
                return new ResponseMessage(false, "you don't have channel, but you can create it", principal);
            }
        }else {
            return new ResponseMessage(true, "Here is the channel", principal.getChannel());
        }
    }

    public ResponseMessage createChannel(ChannelDTO channelDTO){
        User principal = getCurrentPrincipal();
        Channel channel = principal.getChannel();
        if (channel == null) {
            Optional<Channel> optionalChannel = channelRepository.findByOwnerId(principal.getId());
            if (optionalChannel.isPresent()) {
                return new ResponseMessage(false, "you already have a channel", channel);
            }else {
                Channel newChannel = Channel.builder()
                        .owner(principal)
                        .description(channelDTO.getDescription())
                        .profilePicture(channelDTO.getProfilePicture())
                        .name(channelDTO.getName())
                        .build();
                principal.setChannel(newChannel);
                channelRepository.save(newChannel);
                userRepository.save(principal);
                return new ResponseMessage(true, "channel created", newChannel);
            }
        }else {
            return new ResponseMessage(false, "you already have a channel", channel);
        }
    }


    @Transactional
    public ResponseMessage deleteChannel() {
       User principal = getCurrentPrincipal();
       if (principal.getChannel() == null) {
           Optional<Channel> optionalChannel = channelRepository.findByOwnerId(principal.getId());
           if (optionalChannel.isPresent()) {
               Channel channel = optionalChannel.get();
               channelRepository.delete(channel);
               return new ResponseMessage(true, "channel deleted", channel);
           }else {
               return new ResponseMessage(false, "you don't have a channel", principal);
           }

       }else {
           channelRepository.deleteById(principal.getChannel().getId());
           System.out.println(principal.getChannel().getId());
           return new ResponseMessage(true, "channel deleted without rep", principal.getChannel());
       }
    }
}

