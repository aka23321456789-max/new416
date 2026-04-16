// 生成指定长度的随机验证码（包含数字和大小写字母）
export const generateCaptcha = (length = 5) => {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
  let captcha = ''
  for (let i = 0; i < length; i++) {
    const randomIndex = Math.floor(Math.random() * chars.length)
    captcha += chars[randomIndex]
  }
  return captcha
}

// 验证验证码是否正确
export const validateCaptcha = (input, captcha) => {
  return input.toLowerCase() === captcha.toLowerCase()
}
