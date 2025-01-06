-- CreateTable
CREATE TABLE "UserReviews" (
    "id" SERIAL NOT NULL,
    "reviewer_user_id" INTEGER NOT NULL,
    "reviewee_user_id" INTEGER NOT NULL,
    "review_comment_id" INTEGER NOT NULL,
    "like" BOOLEAN NOT NULL,

    CONSTRAINT "UserReviews_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ReviewComments" (
    "id" SERIAL NOT NULL,
    "comment" TEXT NOT NULL,

    CONSTRAINT "ReviewComments_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "UserReviews" ADD CONSTRAINT "UserReviews_reviewer_user_id_fkey" FOREIGN KEY ("reviewer_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "UserReviews" ADD CONSTRAINT "UserReviews_reviewee_user_id_fkey" FOREIGN KEY ("reviewee_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "UserReviews" ADD CONSTRAINT "UserReviews_review_comment_id_fkey" FOREIGN KEY ("review_comment_id") REFERENCES "ReviewComments"("id") ON DELETE CASCADE ON UPDATE CASCADE;
