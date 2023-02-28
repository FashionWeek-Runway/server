package com.example.runway.repository;

import com.example.runway.domain.StoreReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreReviewRepository extends JpaRepository<StoreReview,Long> {
    Page<StoreReview> findByStoreIdAndStatusOrderByCreatedAtDescIdAsc(Long storeId,  boolean b,Pageable pageReq);

    @Query(nativeQuery = true,value="select SR.id   'reviewId',SR.user_id'userId',\n" +
            "       U.profile_url 'profileImgUrl',\n" +
            "       U.nickname,\n" +
            "       SR.img_url    'imgUrl',\n" +
            "       SR.store_id   'storeId',\n" +
            "       S.name        'storeName',\n" +
            "       concat(R.region,', ',R.city)'regionInfo'," +
            "       SR.created_at'createdAt'," +
            "       IF((select exists(select * from ReviewKeep RK where RK.review_id=:reviewId and RK.user_id=:userId)),'true','false')'bookmark'," +
            "       count(K.review_id)'bookmarkCnt'\n" +
            "from StoreReview SR\n" +
            "         join User U on U.id = SR.user_id\n" +
            "         join Store S on S.id = SR.store_id\n" +
            "         join Region R on R.id = S.region_id\n" +
            "         join ReviewKeep K on K.review_id=:reviewId " +
            "where SR.id=:reviewId and SR.status=true")
    StoreReviewRepository.GetStoreReview getStoreReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    boolean existsByIdAndStatus(Long reviewId, boolean b);


    @Query(value = "select SR.id from StoreReview SR" +
            " where SR.user_id=:userId and SR.created_at>:createdAt and SR.id != :reviewId and SR.status =true  order by created_at asc limit 1",nativeQuery = true)
    GetReviewId findPrevMuReviewId(@Param("createdAt") LocalDateTime createdAt,@Param("userId") Long userId,@Param("reviewId") Long reviewId);

    @Query(value = "select SR.id from StoreReview SR" +
            " where SR.user_id=:userId and SR.created_at<:createdAt and SR.id != :reviewId and SR.status =true order by created_at desc limit 1",nativeQuery = true)
    GetReviewId findNextMyReviewId(@Param("createdAt") LocalDateTime createdAt,@Param("userId") Long userId,@Param("reviewId") Long reviewId);

    @Query(value = "select SR.id         'reviewId',SR.user_id'userId',\n" +
            "       U.profile_url 'profileImgUrl',\n" +
            "       U.nickname,\n" +
            "       SR.img_url    'imgUrl',S.id'storeId',\n" +
            "       S.name        'storeName',\n" +
            "       concat(R.region, ', ', R.city)'regionInfo',\n" +
            "       count(RK.review_id)'bookmarkCnt',SR.created_at'createdAt'\n" +
            "from StoreReview SR\n" +
            "         join User U on SR.user_id = U.id\n" +
            "         join Store S on SR.store_id = S.id\n" +
            "         join Region R on S.region_id = R.id\n" +
            "         left join ReviewKeep RK on SR.id = RK.review_id\n" +
            "where SR.id=:reviewId and RK.review_id=SR.id and RK.user_id=:userId and SR.status =true ",nativeQuery = true)
    GetStoreReview getMyBookmarkReview(@Param("reviewId") Long reviewId,@Param("userId") Long userId);


    @Query(value = "select SR.id\n" +
            "from StoreReview SR\n" +
            "join ReviewKeep RK on SR.id = RK.review_id " +
            "where RK.user_id=:userId and SR.created_at < :createdAt\n" +
            "   or (created_at = :createdAt AND id > :reviewId)\n" +
            "  and SR.id != :reviewId and SR.status =true \n" +
            "order by created_at desc, SR.id desc limit 1",nativeQuery = true)
    GetReviewId findNextBookMarkReviewId(@Param("createdAt") LocalDateTime createdAt,@Param("userId") Long userId,@Param("reviewId") Long reviewId);

    @Query(value = "select SR.id \n" +
            "from StoreReview SR\n" +
            "join ReviewKeep RK on SR.id = RK.review_id " +
            "where RK.user_id=:userId and SR.created_at > :createdAt\n" +
            "   or (created_at = :createdAt AND id<:reviewId)\n" +
            "    and SR.id != :reviewId and SR.status =true \n" +
            "order by SR.created_at asc, SR.id desc limit 1 ",nativeQuery = true)
    GetReviewId findPrevBookMarkReviewId(@Param("createdAt") LocalDateTime createdAt,@Param("userId") Long userId,@Param("reviewId") Long reviewId);

    Optional<Object> findByIdAndStatus(Long reviewId, boolean b);


    interface GetCountAllReview {
        int getSize();
    }
    @Query(value = "select SR.id \n" +
                "from StoreReview SR\n" +
                "where SR.store_id = :storeId\n" +
                "  and SR.created_at > :createdAt\n" +
                "   or (created_at = :createdAt AND id<:reviewId)\n" +
                "    and SR.id != :reviewId and SR.status =true \n" +
                "order by created_at asc, SR.id desc limit 1",nativeQuery = true)
    StoreReviewRepository.GetReviewId findPrevReviewId(@Param("createdAt") LocalDateTime createdAt,@Param("storeId") Long storeId,@Param("reviewId") Long reviewId);


    @Query(value = "select SR.id\n" +
            "from StoreReview SR\n" +
            "where SR.store_id = :storeId\n" +
            "  and SR.created_at < :createdAt\n" +
            "   or (created_at = :createdAt AND id > :reviewId)\n" +
            "  and SR.id != :reviewId and SR.status =true \n " +
            "order by created_at desc, SR.id desc limit 1",nativeQuery = true)
    StoreReviewRepository.GetReviewId findNextReviewId(@Param("createdAt") LocalDateTime createdAt, @Param("storeId") Long storeId, @Param("reviewId") Long reviewId);


    @Query(value = "select SR.id         'reviewId',SR.user_id'userId',\n" +
            "       U.profile_url 'profileImgUrl',\n" +
            "       U.nickname,\n" +
            "       SR.img_url    'imgUrl',S.id'storeId',\n" +
            "       S.name        'storeName',\n" +
            "       concat(R.region, ', ', R.city)'regionInfo',\n" +
            "       count(RK.review_id)'bookmarkCnt',SR.created_at'createdAt'\n" +
            "from StoreReview SR\n" +
            "         join User U on SR.user_id = U.id\n" +
            "         join Store S on SR.store_id = S.id\n" +
            "         join Region R on S.region_id = R.id\n" +
            "         left join ReviewKeep RK on SR.id = RK.review_id\n" +
            "where SR.id=:reviewId and SR.status =true ",nativeQuery = true)
    GetStoreReview getMyReview(@Param("reviewId") Long reviewId);
    interface GetStoreReview {
        Long getReviewId();
        Long getUserId();
        String getProfileImgUrl();
        String getNickname();
        String getImgUrl();
        Long getStoreId();
        String getStoreName();
        String getRegionInfo();
        LocalDateTime getCreatedAt();
        boolean getBookMark();
        int getBookmarkCnt();
    }


    @Query(value = "select SR.id 'reviewId', SR.img_url'imgUrl',concat(R.region,'/',R.city)'regionInfo'\n" +
            "from StoreReview SR\n" +
            "join Store S on SR.store_id = S.id\n" +
            "join Region R on S.region_id = R.id\n" +
            "where SR.user_id = :userId and SR.status = true order by SR.created_at desc",
            countQuery = "select count(*) from StoreReview SR where SR.user_id=:userId and SR.status=true",nativeQuery = true)
    Page<StoreReviewRepository.GetReviewInfo> GetReviewInfo(@Param("userId") Long userId, Pageable pageReq);
    interface GetReviewInfo {
        Long getReviewId();
        String getImgUrl();
        String getRegionInfo();
    }


    interface GetReviewId{
        Long getId();
    }

    @Query(value =
            "select SR.id                          'reviewId',\n" +
                    "       SR.img_url                     'imgUrl',\n" +
                    "       concat(R.region, ', ', R.city) 'regionInfo',\n" +
                    "       IF((select exists(select * from ReviewRead where ReviewRead.user_id = :userId and ReviewRead.review_id = SR.id)),\n" +
                    "          'true', 'false')            'isRead',\n" +
                    "       count(K.user_id)'bookmarkCnt',S.name,\n" +
                    "       SUM(CASE WHEN C.category IN (:categoryList) THEN 1 ELSE 0 END) AS categoryScore\n" +
                    "from Store S\n" +
                    "         join StoreCategory SC on S.id = SC.store_id\n" +
                    "         join Category C on SC.category_id = C.id\n" +
                    "         join StoreReview SR on S.id = SR.store_id\n" +
                    "         join Region R on S.region_id = R.id\n" +
                    "         left join Keep K on S.id = K.store_id\n" +
                    "where C.category IN (:categoryList) and SR.status=true\n" +
                    "group by SR.id order by categoryScore DESC,bookmarkCnt DESC,SR.id asc",
            countQuery = "select count(DISTINCT StoreReview.id) from StoreReview join Store S on StoreReview.store_id = S.id" +
                        " join StoreCategory SC on S.id = SC.store_id join Category C on SC.category_id = C.id" +
                        " where C.category IN(:categoryList) and StoreReview.status=true ",
            nativeQuery = true)
    Page<GetReview> RecommendReview(@Param("userId") Long userId, @Param("categoryList") List<String> categoryList, Pageable pageReq);
    interface GetReview {
        Long getReviewId();
        String getImgUrl();
        String getRegionInfo();
        Boolean getIsRead();
    }


    @Query(value = "select SR.id'reviewId', SR.img_url'imgUrl',concat(R.region,'/',R.city)'regionInfo',SR.created_at " +
            "from StoreReview SR " +
            "join ReviewKeep RK on SR.id = RK.review_id " +
            "join Store S on SR.store_id = S.id " +
            "join Region R on S.region_id = R.id " +
            "where RK.user_id=:userId and SR.status = true order by SR.created_at desc",countQuery = "select count(*) from StoreReview SR join ReviewKeep RK on SR.id = RK.review_id where RK.user_id=:userId",nativeQuery = true)
    Page<GetReviewInfo> getMyBookMarkReview(@Param("userId") Long userId, Pageable pageReq);


}
