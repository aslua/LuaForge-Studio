package com.nwdxlgzs.AxmlEditor;

import com.nwdxlgzs.AxmlEditor.fix.EntryPoint;
import com.nwdxlgzs.AxmlEditor.rt.NodeVisitor;
import com.nwdxlgzs.AxmlEditor.rt.Reader;
import com.nwdxlgzs.AxmlEditor.rt.Util;
import com.nwdxlgzs.AxmlEditor.rt.Visitor;
import com.nwdxlgzs.AxmlEditor.rt.Writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AxmlEditor {
    private final String[] components = {"activity", "activity-alias", "provider", "receiver", "service", "intent-filter", "data"};
    private File mManifest;
    private int mVersionCode = -1;
    private int mMinimumSdk = -1;
    private int mTargetSdk = -1;
    private int mInstallLocation = -1;
    private String mVersionName;
    private String mAppName;
    private String mPackageName;
    private byte[] mManifestData;
    private String[] permissions;
    private boolean changeHost = true;
    private String targetScheme;
    private String replaceScheme;
    private String[] providerKeys;
    private String[] providerRaws;
    private String[] providerNews;

    public AxmlEditor(File manifest) {
        mManifest = manifest;
    }

    public AxmlEditor(InputStream manifest) throws IOException {
        mManifestData = Util.readIs(manifest);
    }

    public AxmlEditor(byte[] manifest) {
        mManifestData = manifest;
    }

    private boolean usefix = false;

    public void setUseFix(boolean use) {
        usefix = use;
    }

    public void setChangeScheme(String target, String new_scheme) {
        targetScheme = target;
        replaceScheme = new_scheme;
    }

    public void setChangeHostIfLikePackageName(boolean change) {
        changeHost = change;
    }

    public void setUsePermissions(String[] list) {
        permissions = list;
    }

    public void setVersionCode(int versionCode) {
        mVersionCode = versionCode;
    }

    public void setVersionName(String versionName) {
        mVersionName = versionName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public void setMinimumSdk(int sdk) {
        mMinimumSdk = sdk;
    }

    public void setTargetSdk(int sdk) {
        mTargetSdk = sdk;
    }

    public void setInstallLocation(int location) {
        mInstallLocation = location;
    }

    public void setTargetReplaceAppName(String name) {
        needAutoGuessTarget = false;
        targetReplaceAppName = name;
    }

    public void setTargetReplacePackageName(String name) {
        needAutoGuessTarget = false;
        targetReplacePackageName = name;
    }

    public void setProviderHandleTask(String[] keys, String[] raws, String[] news) {
        providerKeys = keys;
        providerRaws = raws;
        providerNews = news;
    }

    private boolean permissionLoopOK = false;
    private String targetReplaceAppName;
    private String targetReplacePackageName;
    private boolean needAutoGuessTarget = true;

    public void commit() throws IOException {
        Reader reader = new Reader(mManifestData == null ? Util.readFile(mManifest) : mManifestData);
        Writer writer = new Writer();
        permissionLoopOK = false;
        if (needAutoGuessTarget) {
            targetReplaceAppName = null;
            targetReplacePackageName = null;
        }
        reader.accept(new Visitor(writer) {
            public NodeVisitor child(String ns, String name) {
                return new NodeVisitor(super.child(ns, name)) {
                    public NodeVisitor child(String ns, String name) {
                        // 处理 uses-permission 标签
                        if (name.equalsIgnoreCase("uses-permission")) {
                            if (permissions == null) {
                                return new NodeVisitor(super.child(ns, name)) {
                                    @Override
                                    public void attr(String ns, String name, int resourceId, int type, Object value) {
                                        // 替换权限名称中的包名
                                        if (name.equalsIgnoreCase("name") && value instanceof String permissionName
                                                && mPackageName != null && targetReplacePackageName != null) {
                                            // 检查是否是自定义权限（包含包名）
                                            if (permissionName.startsWith(targetReplacePackageName + ".")) {
                                                value = mPackageName + permissionName.substring(targetReplacePackageName.length());
                                                type = TYPE_STRING;
                                            }
                                        }
                                        super.attr(ns, name, resourceId, type, value);
                                    }
                                };
                            }
                            if (!permissionLoopOK) {
                                for (String permission : permissions) {
                                    super.child(ns, name).attr("http://schemas.android.com/apk/res/android", "name", 16842755, 3, permission);
                                }
                                permissionLoopOK = true;
                            }
                            return null;
                        } else if (name.equalsIgnoreCase("permission")) {
                            // 处理 permission 标签
                            return new NodeVisitor(super.child(ns, name)) {
                                @Override
                                public void attr(String ns, String name, int resourceId, int type, Object value) {
                                    // 替换权限名称中的包名
                                    if (name.equalsIgnoreCase("name") && value instanceof String permissionName
                                            && mPackageName != null && targetReplacePackageName != null) {
                                        // 检查是否是自定义权限（包含包名）
                                        if (permissionName.startsWith(targetReplacePackageName + ".")) {
                                            value = mPackageName + permissionName.substring(targetReplacePackageName.length());
                                            type = TYPE_STRING;
                                        }
                                    }
                                    super.attr(ns, name, resourceId, type, value);
                                }
                            };
                        } else if (name.equalsIgnoreCase("uses-sdk")) {
                            return new NodeVisitor(super.child(ns, name)) {
                                @Override
                                public void attr(String ns, String name, int resourceId, int type, Object value) {
                                    if (name.equalsIgnoreCase("minSdkVersion") && mMinimumSdk > 0) {
                                        value = mMinimumSdk;
                                        type = TYPE_FIRST_INT;
                                    } else if (name.equalsIgnoreCase("targetSdkVersion") && mTargetSdk > 0) {
                                        value = mTargetSdk;
                                        type = TYPE_FIRST_INT;
                                    }
                                    super.attr(ns, name, resourceId, type, value);
                                }
                            };
                        } else if (name.equalsIgnoreCase("application")) {
                            return new NodeVisitor(super.child(ns, name)) {
                                public NodeVisitor child(String ns, String name) {
                                    if (name.equalsIgnoreCase("activity")) {
                                        return new NodeVisitor(super.child(ns, name)) {
                                            @Override
                                            public void attr(String ns, String name, int resourceId, int type, Object value) {
                                                if (name.equalsIgnoreCase("label") && mAppName != null && value.equals(targetReplaceAppName)) {
                                                    value = mAppName;
                                                    type = TYPE_STRING;
                                                }
                                                super.attr(ns, name, resourceId, type, value);
                                            }

                                            public NodeVisitor child(String ns, String name) {
                                                if (name.equalsIgnoreCase("intent-filter") && targetReplacePackageName != null && mPackageName != null) {
                                                    return new NodeVisitor(super.child(ns, name)) {
                                                        public NodeVisitor child(String ns, String name) {
                                                            if (name.equalsIgnoreCase("data")) {
                                                                return new NodeVisitor(super.child(ns, name)) {
                                                                    @Override
                                                                    public void attr(String ns, String name, int resourceId, int type, Object value) {
                                                                        if (name.equalsIgnoreCase("host") && changeHost && value.equals(targetReplacePackageName)) {
                                                                            value = mPackageName;
                                                                            type = TYPE_STRING;
                                                                        } else if (name.equalsIgnoreCase("scheme") && targetScheme != null && replaceScheme != null) {
                                                                            value = replaceScheme;
                                                                            type = TYPE_STRING;
                                                                        }
                                                                        super.attr(ns, name, resourceId, type, value);
                                                                    }
                                                                };
                                                            }
                                                            return super.child(ns, name);
                                                        }

                                                    };
                                                }
                                                return super.child(ns, name);
                                            }

                                        };
                                    }

                                    for (String component : components) {
                                        if (name.equalsIgnoreCase(component)) {
                                            final String innerTag = component;
                                            return new NodeVisitor(super.child(ns, name)) {
                                                @Override
                                                public void attr(String ns, String name, int resourceId, int type, Object value) {
                                                    if (name.equalsIgnoreCase("name") && value instanceof String && mPackageName != null) {
                                                        int check = ((String) value).indexOf(".");
                                                        if (check < 0) {
                                                            value = mPackageName + "." + value;
                                                        } else if (check == 0) {
                                                            value = mPackageName + value;
                                                        }
                                                        type = TYPE_STRING;
                                                    } else if (innerTag.equalsIgnoreCase("provider") && name.equalsIgnoreCase("authorities") && mPackageName != null && targetReplacePackageName != null) {
                                                        // 重要修复：支持authorities以原始包名作为前缀的替换
                                                        if (value instanceof String authorities) {
                                                            // 检查是否以原始包名开头
                                                            if (authorities.startsWith(targetReplacePackageName)) {
                                                                // 替换整个字符串
                                                                value = mPackageName + authorities.substring(targetReplacePackageName.length());
                                                                type = TYPE_STRING;
                                                            }
                                                        }
                                                    }
                                                    //单独设置的事件，以他为准
                                                    if (innerTag.equalsIgnoreCase("provider") && providerKeys != null && providerNews != null && providerRaws != null) {
                                                        for (int i = 0; i < providerKeys.length; i++) {
                                                            if (name.equalsIgnoreCase(providerKeys[i])) {
                                                                if (value.equals(providerRaws[i])) {
                                                                    value = providerNews[i];
                                                                    type = TYPE_STRING;
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    super.attr(ns, name, resourceId, type, value);
                                                }
                                            };
                                        }
                                    }
                                    return super.child(ns, name);
                                }

                                @Override
                                public void attr(String ns, String name, int resourceId, int type, Object value) {
                                    if (targetReplaceAppName == null) {
                                        targetReplaceAppName = String.valueOf(value);
                                    }
                                    if (name.equalsIgnoreCase("label") && mAppName != null) {
                                        value = mAppName;
                                        type = TYPE_STRING;
                                    }  //return;

                                    super.attr(ns, name, resourceId, type, value);
                                }
                            };
                        }
                        return super.child(ns, name);
                    }

                    @Override
                    public void attr(String ns, String name, int resourceId, int type, Object value) {
                        // 处理 permission 标签的包名替换（manifest级别的permission）
                        if (name.equalsIgnoreCase("name") && value instanceof String attrValue
                                && mPackageName != null && targetReplacePackageName != null) {
                            // 检查是否是自定义权限（包含包名）
                            if (attrValue.startsWith(targetReplacePackageName + ".")) {
                                String suffix = attrValue.substring(targetReplacePackageName.length());
                                value = mPackageName + suffix;
                                type = TYPE_STRING;
                            }
                        }

                        if (name.equalsIgnoreCase("package") && mPackageName != null) {
                            targetReplacePackageName = String.valueOf(value);
                            value = mPackageName;
                            type = TYPE_STRING;
                        } else if (name.equalsIgnoreCase("installLocation")) {
                            int loc = getRealInstallLocation(mInstallLocation);
                            if (loc >= 0) {
                                value = loc;
                                type = TYPE_FIRST_INT;
                            } else {
                                return;
                            }
                        } else if (name.equalsIgnoreCase("versionName") && mVersionName != null) {
                            value = mVersionName;
                            type = TYPE_STRING;
                        } else if (name.equalsIgnoreCase("versionCode") && mVersionCode > 0) {
                            value = mVersionCode;
                            type = TYPE_FIRST_INT;
                        }
                        super.attr(ns, name, resourceId, type, value);
                    }
                };
            }
        });
        mManifestData = usefix ? EntryPoint.fix(writer.toByteArray()) : writer.toByteArray();
    }

    // 专门修复权限名称和provider的authorities
    public void fixPermissionNames(String originalPackageName, String newPackageName) {
        // 这个方法可以在 commit() 之后调用，专门修复权限名称和provider的authorities
        if (originalPackageName == null || newPackageName == null) {
            return;
        }

        try {
            Reader reader = new Reader(mManifestData);
            Writer writer = new Writer();

            reader.accept(new Visitor(writer) {
                @Override
                public NodeVisitor child(String ns, String name) {
                    return new NodeVisitor(super.child(ns, name)) {
                        @Override
                        public NodeVisitor child(String ns, String name) {
                            // 处理 provider 标签的authorities属性
                            if (name.equalsIgnoreCase("provider")) {
                                return new NodeVisitor(super.child(ns, name)) {
                                    @Override
                                    public void attr(String ns, String name, int resourceId,
                                                     int type, Object value) {
                                        if (name.equalsIgnoreCase("authorities") && value instanceof String authorities) {
                                            // 检查是否以原始包名开头
                                            if (authorities.startsWith(originalPackageName)) {
                                                // 替换整个字符串
                                                value = newPackageName + authorities.substring(originalPackageName.length());
                                                type = TYPE_STRING;
                                                System.out.println("修复provider authorities: " + authorities + " -> " + value);
                                            }
                                        }
                                        super.attr(ns, name, resourceId, type, value);
                                    }
                                };
                            }
                            // 处理 permission 和 uses-permission 标签
                            if (name.equalsIgnoreCase("permission") ||
                                    name.equalsIgnoreCase("uses-permission")) {
                                return new NodeVisitor(super.child(ns, name)) {
                                    @Override
                                    public void attr(String ns, String name, int resourceId,
                                                     int type, Object value) {
                                        if (name.equalsIgnoreCase("name") && value instanceof String permissionName) {
                                            // 替换包名部分
                                            if (permissionName.startsWith(originalPackageName + ".")) {
                                                String suffix = permissionName.substring(originalPackageName.length());
                                                value = newPackageName + suffix;
                                                type = TYPE_STRING;
                                            }
                                        }
                                        super.attr(ns, name, resourceId, type, value);
                                    }
                                };
                            }
                            return super.child(ns, name);
                        }

                        @Override
                        public void attr(String ns, String name, int resourceId, int type, Object value) {
                            // 处理 manifest 级别的权限属性
                            if (name.equalsIgnoreCase("name") && value instanceof String attrValue) {
                                // 检查是否是需要替换的权限名称
                                if (attrValue.startsWith(originalPackageName + ".")) {
                                    String suffix = attrValue.substring(originalPackageName.length());
                                    value = newPackageName + suffix;
                                    type = TYPE_STRING;
                                }
                            }
                            super.attr(ns, name, resourceId, type, value);
                        }
                    };
                }
            });

            mManifestData = writer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTo(FileOutputStream manifestOutputStream) throws IOException {
        manifestOutputStream.write(mManifestData);
        manifestOutputStream.close();
    }

    public void writeTo(OutputStream manifestOutputStream) throws IOException {
        manifestOutputStream.write(mManifestData);
    }

    /*
     Return real install location from selected item in spinner
     */
    private int getRealInstallLocation(int installLocation) {
        return switch (installLocation) {
            case 0 -> -1;//default
            case 1 -> 0;//auto
            case 2 -> 1;//internal
            case 3 -> 2;//external
            default -> -1;
        };
    }
}