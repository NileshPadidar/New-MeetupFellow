//package com.oit.reegur.developement.implementation
//
//import android.content.Context
//import com.amazonaws.auth.CognitoCachingCredentialsProvider
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
//import com.amazonaws.regions.Region
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.s3.AmazonS3Client
//import com.oit.reegur.constants.Constants
//import com.oit.reegur.developement.interfaces.AwsS3Util
//
///**
// * Created by Maheshwar on 13-09-2017.
// */
//class AwsS3UtilImpl(private val context: Context) : AwsS3Util {
//
//    // We only need one instance of the clients and credentials provider
//    private var sS3Client: AmazonS3Client? = null
//    private var sCredProvider: CognitoCachingCredentialsProvider? = null
//    private var sTransferUtility: TransferUtility? = null
//
//    override fun getCredProvider(): CognitoCachingCredentialsProvider {
//        if (sCredProvider == null) {
//            sCredProvider = CognitoCachingCredentialsProvider(
//                    context.applicationContext,
//                    Constants.AWS.PoolId.COGNITO_POOL_ID,
//                    Regions.fromName(Constants.AWS.PoolRegion.COGNITO_POOL_REGION))
//        }
//        return sCredProvider as CognitoCachingCredentialsProvider
//    }
//
//    override fun getS3Client(): AmazonS3Client {
//        if (sS3Client == null) {
//            sS3Client = AmazonS3Client(getCredProvider())
//            sS3Client!!.setRegion(Region.getRegion(Regions.fromName(
//                    Constants.AWS.BucketRegion.AWS_BUCKET_REGION)))
//        }
//        return sS3Client as AmazonS3Client
//    }
//
//    override fun getTransferUtility(): TransferUtility {
//        if (sTransferUtility == null) {
//            sTransferUtility = TransferUtility(getS3Client(),
//                    context.applicationContext)
//        }
//
//        return sTransferUtility as TransferUtility
//    }
//
//}