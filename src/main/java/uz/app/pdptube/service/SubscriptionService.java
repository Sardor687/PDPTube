package uz.app.pdptube.service;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.entity.*;
import uz.app.pdptube.enums.NotificationType;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    public ResponseMessage getSubscribers() {
        Integer currentUserId = Helper.getCurrentPrincipal().getId();

        //Chanel egasini tekshirish
        Optional<ChannelOwner> channelOwner = channelOwnerRepository.findByOwner(currentUserId);
        if (channelOwner.isEmpty()) {
            return new ResponseMessage(false, "You don't own a channel, please create channel", null);
        }

        //Channel idsini olish
        Integer channelId = channelOwner.get().getChannel();

        //Channelga obuna foyga foydalanuvchilarni olish
        List<Subscription> subscriptions = subscriptionRepository.findAllByChannel(channelId);

        if (subscriptions.isEmpty()) {
            return new ResponseMessage(true, "No subscribers found for this channel", List.of());
        }

        //follower id laridan foydalanuvchi malumotlarini olish
        List<User> subscribers = subscriptions.stream()
                .map(subscription -> userRepository.findById(subscription.getFollower()))
                .filter(user -> user.isPresent())
                .map(user -> user.get())
                .collect(Collectors.toList());

        if (subscriptions.size() == subscribers.size()) {
            return new ResponseMessage(true, "Here are the subscribers of this channel", subscribers);
        } else {
            return new ResponseMessage(true, "Here are the subscribers of this channel, back end subscribers relationni user exists ga tekshirsin!", subscribers);
        }
    }


    public ResponseMessage getSubscriptions() {
        User principal = Helper.getCurrentPrincipal();
        List<Subscription> userSubscriptions = subscriptionRepository.findAllByFollower(principal.getId());
        if (userSubscriptions.isEmpty()) {
            return new ResponseMessage(true, "Seems like you haven't followed anyone yet", userSubscriptions);
        } else {
            List<Integer> channelIds = userSubscriptions.stream().map(sub -> sub.getChannel()).toList();
            List<Channel> channels = new ArrayList<>();
            for (Integer channelId : channelIds)
                channelRepository.findById(channelId).ifPresent(channel -> channels.add(channel));

            return new ResponseMessage(true, "Here are all of the channels you have subscribed to", channels);
        }
    }


    public ResponseMessage subscribeToChannel(Integer channelId) {
        User currentUser = Helper.getCurrentPrincipal();
        if (!channelRepository.findById(channelId).isPresent()) {
            return new ResponseMessage(false, "Channel with this idea doesn't exist", channelId);
        }


        Optional<ChannelOwner> channelOwner = channelOwnerRepository.findByChannel(channelId);
        if (channelOwner.isEmpty()) {
            return new ResponseMessage(false, "channelOwner relationda yoq , lekin Channel bor , backenda xato", channelId);
        } else {


            if (channelOwner.get().getOwner().equals(currentUser.getId())) {
                return new ResponseMessage(false, "You cannot subscribe to your own channel", null);
            }


            boolean existingSubscription = subscriptionRepository.existsByChannelAndFollower(channelId, currentUser.getId());
            if (existingSubscription) {
                return new ResponseMessage(false, "You are already subscribed to this channel", channelId);
            }


            ChannelOwner relation = channelOwner.get();
            Integer ownerId = relation.getOwner();
            Subscription newFollower = new Subscription();
            newFollower.setChannel(channelId);
            newFollower.setFollower(currentUser.getId());
            subscriptionRepository.save(newFollower);
            Notification notification = Notification.builder()
                    .type(NotificationType.NEW_FOLLOWER)
                    .subjectId(Helper.getCurrentPrincipal().getId())
                    .userId(ownerId)
                    .build();
            notificationRepository.save(notification);

            return new ResponseMessage(true, "Successfully subscribed to the channel", newFollower);
        }

    }

    @Transactional
    public ResponseMessage unsubscribeFromChannel(Integer channelId) {
        User currentUser = Helper.getCurrentPrincipal();

        Optional<Subscription> subscription = subscriptionRepository.findByChannelAndFollower(channelId, currentUser.getId());

        if (subscription.isEmpty()) {
            return new ResponseMessage(false, "Subscription not found", null);
        }

        subscriptionRepository.delete(subscription.get());

        return new ResponseMessage(true, "Successfully unsubscribed from the channel", null);
    }
}
