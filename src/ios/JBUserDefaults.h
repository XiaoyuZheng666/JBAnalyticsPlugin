//
//  JBUserDefaults.h
//  黄金象
//
//  Created by MAC005 on 2019/1/18.
//

#import <Foundation/Foundation.h>

@interface JBUserDefaults : NSObject

//用来保存记录的eventid
+(NSMutableArray *)getEventIds;
+(void)setEventIds:(NSMutableArray *)datas;

+(NSMutableArray*)getRecordsWithKey:(NSString*)key;
+(void)setRecords:(NSMutableArray*)datas withKey:(NSString*)key;

+(NSMutableArray*)getLaunchs;
+(void)setLaunchs:(NSMutableArray*)datas;

@end
