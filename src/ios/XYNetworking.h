//
//  XYNetworking.h
//  黄金象
//
//  Created by MAC005 on 2019/1/21.
//

#import <Foundation/Foundation.h>

@interface XYNetworking : NSObject

/**
 * 原生get请求（NSURLSession）JSON解析
 */
+ (void)getRequestByServiceUrl:(NSString *)service
                        andApi:(NSString *)api
                     andParams:(NSDictionary *)params
                   andCallBack:(void (^)(id obj))callback;

/**
 * 原生post请求（NSURLSession）JSON解析
 */

+ (void)postRequestByServiceUrl:(NSString *)service
                         andApi:(NSString *)api
                      andParams:(NSDictionary *)params andResponseHeader:(void (^)(id obj))responseHeader
                    andCallBackBody:(void (^)(id obj))callbackBody;

/**
 * 原生上传图片
 */
+ (void) postImageWithBaseApi:(NSString *)baseApi
                   AndPragram:(NSDictionary *)pragram
                   updatImage:(UIImage *)image
                   Completion:(void (^) (id obj))completion;

@end

