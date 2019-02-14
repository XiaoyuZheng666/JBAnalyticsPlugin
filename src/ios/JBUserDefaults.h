//
//  JBUserDefaults.h
//  黄金象
//
//  Created by MAC005 on 2019/1/18.
//

#import <Foundation/Foundation.h>

@interface JBUserDefaults : NSObject



+(NSMutableArray*)getFailedLaunchs;
+(void)setFailedLaunchs:(NSMutableArray*)datas;

@end
